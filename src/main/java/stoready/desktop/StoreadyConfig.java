package stoready.desktop;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;

import org.apache.commons.dbcp2.BasicDataSource;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.spring4.properties.EncryptablePreferencesPlaceholderConfigurer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.MySqlDialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.muhammadkholidb.sequel.config.SequelConfig;
import com.gitlab.muhammadkholidb.toolbox.jackson.ObjectConverter;

@Configuration
@Import(SequelConfig.class)
@ComponentScan
@EnableCaching
@EnableJdbcRepositories
@EnableJdbcAuditing
@EnableTransactionManagement
@EnableAspectJAutoProxy
public class StoreadyConfig extends AbstractJdbcConfiguration {

    @Value("${jdbc.driver}")
    private String jdbcDriver;

    @Value("${jdbc.url}")
    private String jdbcUrl;

    @Value("${jdbc.user}")
    private String jdbcUser;

    @Value("${jdbc.password}")
    private String jdbcPassword;

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(jdbcDriver);
        ds.setUrl(jdbcUrl);
        ds.setUsername(jdbcUser);
        ds.setPassword(jdbcPassword);
        ds.setInitialSize(10);
        ds.setMaxTotal(10);
        return ds;
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Override
    public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
        return MySqlDialect.INSTANCE;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Bean
    public ObjectConverter objectConverter(ObjectMapper objectMapper) {
        return new ObjectConverter(objectMapper);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        ClassicConfiguration config = new ClassicConfiguration();
        config.setDataSource(dataSource());
        return new Flyway(config);
    }

    @Bean
    public Validator validator() {
        return Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Bean
    public static StringEncryptor stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setAlgorithm("PBEWITHMD5ANDTRIPLEDES");
        encryptor.setPassword("46GXyurqh44yMYaOIU7ybYMaEQqUNc4O");
        return encryptor;
    }

    /**
     * Current version of Jasypt (1.9.3) does not support Spring's
     * PropertySourcesPlaceholderConfigurer, the properties value were not
     * decrypted. Just stick with PropertyPlaceholderConfigurer to get the benefit
     * from the encryption of properties values.
     * 
     * @return PropertyPlaceholderConfigurer
     */
    @Bean
    public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        EncryptablePreferencesPlaceholderConfigurer configurer = new EncryptablePreferencesPlaceholderConfigurer(
                stringEncryptor());
        configurer.setLocation(new ClassPathResource("application.properties"));
        return configurer;
    }

}
