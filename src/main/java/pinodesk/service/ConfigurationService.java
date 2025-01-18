package pinodesk.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import pinodesk.annotation.TargetActivity;
import pinodesk.constant.Activity;
import pinodesk.constant.CacheNameConstants;
import pinodesk.constant.DomainError;
import pinodesk.constant.SystemConstants;
import pinodesk.entity.Configuration;
import pinodesk.entity.User;
import pinodesk.exception.DomainException;
import pinodesk.repository.ConfigurationRepository;
import pinodesk.repository.UserRepository;
import pinodesk.viewmodel.UserAddVM;
import pinodesk.util.PasswordUtils;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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

    @Autowired
    private ConfigurationService configurationService;

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    private static final String BACKUP_FILENAME = "backup.zip";
    private static final String BACKUP_FILENAME_ENCRYPTED = "backup.dat";
    private static final String BACKUP_PROPERTIES = "backup.properties";

    @TargetActivity(Activity.GET_CONFIGURATION_BY_CODE)
    @Cacheable(CacheNameConstants.CONFIGURATION_BY_CODE)
    public String getConfiguration(String code) {
        return configurationRepository.findByCodeAndDeletedAtIsNull(code).map(Configuration::getValue).orElse(null);
    }

    @TargetActivity(Activity.GET_CONFIGURATION_MAP)
    @Cacheable(CacheNameConstants.CONFIGURATION_MAP)
    public Map<String, String> getConfigurationMap() {
        return configurationRepository.findByDeletedAtIsNull().stream()
                .collect(Collectors.toMap(Configuration::getCode, Configuration::getValue));
    }

    @CacheEvict(value = { CacheNameConstants.CONFIGURATION_BY_CODE }, allEntries = true)
    @Transactional
    public void saveIntialSetup(Map<String, String> configurationMap, UserAddVM userAdd) {
        configurationService.updateConfiguration(configurationMap);
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
            File backupProperties = createBackupProperties(SystemConstants.USER_HOME_DIR + "/" + BACKUP_PROPERTIES);
            ZipEntry propEntry = new ZipEntry(BACKUP_PROPERTIES);
            zos.putNextEntry(propEntry);
            Files.copy(backupProperties.toPath(), zos);
            zos.closeEntry();
            zos.close();
            File result = new File(location);
            log.debug("Output file created: {}: {}", result.getAbsolutePath(), result.exists());
            Files.delete(backupFile.toPath());
            Files.delete(backupProperties.toPath());
        } catch (IOException e) {
            log.error("Error on create backup", e);
            throw new DomainException(DomainError.BACKUP_DATABASE_ERROR, e.toString());
        }
    }

    private File createBackupProperties(String filename) throws IOException {
        File f = new File(filename);
        FileWriter fw = new FileWriter(f);
        Properties prop = new Properties();
        prop.put("app.name", appName);
        prop.put("app.version", appVersion);
        prop.put("timestamp", String.format("%d", System.currentTimeMillis()));
        prop.store(fw, "DO NOT EDIT!!!");
        fw.close();
        return f;
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
            log.error("Error on restore database", e);
            File dbDirFile = new File(dbDir);
            File dbDirFileOld = new File(dbDirOld);
            if (!dbDirFile.exists() && dbDirFileOld.exists()) {
                boolean renamed = dbDirFileOld.renameTo(dbDirFile);
                log.debug("Renamed old dir to db dir: {}", renamed);
            }
            throw new DomainException(DomainError.RESTORE_DATABASE_ERROR, e.toString());
        }
    }

    @TargetActivity(Activity.UPDATE_CONFIGURATION)
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
                if (BACKUP_PROPERTIES.equals(entry.getName())) {
                    Properties prop = new Properties();
                    prop.load(new ByteArrayInputStream(zis.readAllBytes()));
                    String backupAppVersion = prop.getProperty("app.version");
                    int intCurrentAppVersion = parseVersionToInt(appVersion);
                    log.info("intCurrentAppVersion: {}", intCurrentAppVersion);
                    int intBackupAppVersion = parseVersionToInt(backupAppVersion);
                    log.info("intBackupAppVersion: {}", intBackupAppVersion);
                    // If the app version in the backup file is newer than the current app version,
                    // it will break the app since the database will have the new structure which is
                    // not covered in the previous (current) version
                    if (intBackupAppVersion > intCurrentAppVersion) {
                        throw new DomainException(
                                DomainError.DIFFERENT_BACKUP_APP_VERSION,
                                appVersion,
                                backupAppVersion);
                    }
                }
                if (BACKUP_FILENAME_ENCRYPTED.equals(entry.getName())) {
                    byte[] decrypted = byteEncryptor.decrypt(zis.readAllBytes());
                    String backup = SystemConstants.USER_HOME_DIR + "/" + BACKUP_FILENAME;
                    backupFile = new File(backup);
                    FileCopyUtils.copy(decrypted, backupFile);
                }
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        }
        return backupFile;
    }

    /**
     * Parses the version to an integer so that it can be used for comparison.
     * Previously, this function only remove the dots and return the number as
     * integer, which will break in this example: <br/>
     * <br/>
     * 1.20.1 = 1201 <br/>
     * 2.1.1 = 211 <br/>
     * 1202 > 211 (incorrect) <br/>
     * <br/>
     * The new logic will remove the dots in the version, and the numbers will be
     * padded with 0 in the left so that the string will have the same length. For
     * example: <br/>
     * <br/>
     * 1.20.1 = 001020001 = 1020001 <br/>
     * 2.1.1 = 002001001 = 2001001 <br/>
     * 2001001 > 1020001 (correct) <br/>
     * 
     * @param version the version to parse to integer
     * 
     * @return the integer version
     */
    private int parseVersionToInt(String version) {
        String sub = StringUtils.substringBefore(version, "-");
        String num = Arrays.stream(sub.split("\\.")).map(s -> StringUtils.leftPad(s, 3, "0"))
                .reduce("", String::concat);
        return Integer.valueOf(num);
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
