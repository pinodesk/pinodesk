package pospino.desktop.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import lombok.extern.slf4j.Slf4j;
import pospino.desktop.annotation.ForActivity;
import pospino.desktop.constant.Activity;
import pospino.desktop.constant.CacheNameConstants;
import pospino.desktop.constant.DomainError;
import pospino.desktop.constant.SystemConstants;
import pospino.desktop.domain.Configuration;
import pospino.desktop.domain.User;
import pospino.desktop.exception.DomainException;
import pospino.desktop.repository.ConfigurationRepository;
import pospino.desktop.repository.UserRepository;
import pospino.desktop.util.PasswordUtils;
import pospino.desktop.viewmodel.UserAddVM;

@Slf4j
@Service
public class ConfigurationService extends BaseService {

    @Autowired
    private StandardPBEByteEncryptor byteEncryptor;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String BACKUP_FILENAME = "backup.zip";
    private static final String BACKUP_FILENAME_ENCRYPTED = "backup.dat";

    @ForActivity(Activity.GET_CONFIGURATION_BY_CODE)
    @Cacheable(CacheNameConstants.CONFIGURATION_BY_CODE)
    public String getConfiguration(String code) {
        return configurationRepository.findByCodeAndDeletedAtIsNull(code).map(Configuration::getValue).orElse(null);
    }

    @ForActivity(Activity.GET_CONFIGURATION_MAP)
    @Cacheable(CacheNameConstants.CONFIGURATION_MAP)
    public Map<String, String> getConfigurationMap() {
        return configurationRepository.findByDeletedAtIsNull().stream()
                .collect(Collectors.toMap(Configuration::getCode, Configuration::getValue));
    }

    @CacheEvict(value = { CacheNameConstants.CONFIGURATION_BY_CODE }, allEntries = true)
    @Transactional
    public void saveIntialSetup(Map<String, String> configurationMap, UserAddVM userAdd) {
        updateConfiguration(configurationMap);
        User user = new User();
        user.setFullName(userAdd.getFullName());
        user.setUsername(userAdd.getUsername());
        user.setStatus(userAdd.getStatus().toString());
        user.setUserGroupId(userAdd.getUserGroupId());
        user.setPasswordHash(PasswordUtils.encrypt(userAdd.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void createBackup(String location) {
        try {
            String backup = SystemConstants.USER_HOME_DIR + "/" + BACKUP_FILENAME;
            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement("backup to ?");
                ps.setString(1, backup);
                return ps;
            });
            File backupFile = new File(backup);
            if (!backupFile.exists()) {
                throw new FileNotFoundException(backup);
            }
            log.debug("Backup file created: {}", backupFile.getAbsolutePath());
            byte[] bytes = FileCopyUtils.copyToByteArray(backupFile);
            byte[] encrypted = byteEncryptor.encrypt(bytes);
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(location));
            ZipEntry entry = new ZipEntry(BACKUP_FILENAME_ENCRYPTED);
            zos.putNextEntry(entry);
            zos.write(encrypted);
            zos.closeEntry();
            zos.close();
            File result = new File(location);
            log.debug("Output file created: {}: {}", result.getAbsolutePath(), result.exists());
            Files.delete(backupFile.toPath());
        } catch (IOException e) {
            throw new DomainException(DomainError.BACKUP_DATABASE_ERROR, e.toString());
        }
    }

    public void restoreDatabase(String location) {
        BasicDataSource ds = ((BasicDataSource) dataSource);
        String dirName = getDatabaseDir(ds.getUrl());
        String dbDir = SystemConstants.USER_HOME_DIR + dirName;
        String dbDirOld = SystemConstants.USER_HOME_DIR + dirName + ".old";
        try {
            ds.close();
            File dbDirFile = new File(dbDir);
            File dbDirFileOld = new File(dbDirOld);
            Files.move(dbDirFile.toPath(), dbDirFileOld.toPath(), StandardCopyOption.REPLACE_EXISTING);
            dbDirFile.mkdirs();
            File backupFile = extractEncryptedBackupFile(location);
            if (backupFile == null || !backupFile.exists()) {
                throw new FileNotFoundException(location);
            }
            extractRealBackupFile(backupFile, dbDir);
            boolean deleted = FileSystemUtils.deleteRecursively(dbDirFileOld);
            log.debug("The old dir deleted: {}", deleted);
        } catch (SQLException | IOException e) {
            File dbDirFile = new File(dbDir);
            File dbDirFileOld = new File(dbDirOld);
            if (!dbDirFile.exists() && dbDirFileOld.exists()) {
                boolean renamed = dbDirFileOld.renameTo(dbDirFile);
                log.debug("Renamed old dir to db dir: {}", renamed);
            }
            throw new DomainException(DomainError.RESTORE_DATABASE_ERROR, e.toString());
        }
    }

    @ForActivity(Activity.UPDATE_CONFIGURATION)
    @Transactional
    public void updateConfiguration(Map<String, String> configurationMap) {
        configurationMap.entrySet()
                .forEach(entry -> configurationRepository.updateValueByCode(entry.getKey(), entry.getValue()));
        evictAllCaches();
    }

    private void evictAllCaches() {
        cacheManager.getCacheNames().stream().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    private String getDatabaseDir(String url) {
        return url.substring(url.indexOf("/"), url.lastIndexOf("/"));
    }

    private File extractEncryptedBackupFile(String location) throws EncryptionOperationNotPossibleException,
            IOException {
        File backupFile = null;
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(location))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                if (BACKUP_FILENAME_ENCRYPTED.equals(entry.getName()) && !entry.isDirectory()) {
                    byte[] decrypted = byteEncryptor.decrypt(zis.readAllBytes());
                    String backup = SystemConstants.USER_HOME_DIR + "/" + BACKUP_FILENAME;
                    backupFile = new File(backup);
                    FileCopyUtils.copy(decrypted, backupFile);
                    zis.closeEntry();
                    entry = null;
                }
            }
        }
        return backupFile;
    }

    private void extractRealBackupFile(File backupFile, String dbDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(backupFile))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                if (!entry.isDirectory()) {
                    String db = dbDir + "/" + entry.getName();
                    FileCopyUtils.copy(zis.readAllBytes(), new File(db));
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        }
    }

}
