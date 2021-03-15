package com.getkembang.kembangdesktop;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.muhammadkholidb.sequel.config.SequelConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@Import(SequelConfig.class)
@ComponentScan
@EnableCaching
@EnableTransactionManagement
@PropertySource({ "classpath:application.properties" })
public class KembangConfig {

    @Resource // https://stackoverflow.com/questions/19421092/autowired-environment-is-null
    private Environment env;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(env.getRequiredProperty("jdbc.driver"));
        config.setJdbcUrl(env.getRequiredProperty("jdbc.url"));
        config.setUsername(env.getRequiredProperty("jdbc.user"));
        config.setPassword(env.getRequiredProperty("jdbc.password"));
        return new HikariDataSource(config);
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "configurationByCode",
                "productsByFilter", 
                "productCategoriesByKeyword", 
                "racksAll",
                "racksByKeyword", 
                "unitsAll",
                "unitsByKeyword",
                "drugCategoriesByKeyword",
                "customersByFilter");
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        ClassicConfiguration config = new ClassicConfiguration();
        config.setDataSource(dataSource());
        return new Flyway(config);
    }

}
