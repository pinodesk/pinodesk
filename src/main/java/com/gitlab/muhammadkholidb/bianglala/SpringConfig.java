package com.gitlab.muhammadkholidb.bianglala;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Properties;
import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.OpenJpaDialect;
import org.springframework.orm.jpa.vendor.OpenJpaVendorAdapter;

@Configuration
@ComponentScan
@EnableCaching
@EnableJpaRepositories
@EnableTransactionManagement
@PropertySource({ "classpath:application.properties" })
public class SpringConfig {
    
    @Resource   // https://stackoverflow.com/questions/19421092/autowired-environment-is-null
    private Environment env;

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setUrl(env.getRequiredProperty("jdbc.url"));
        ds.setUsername(env.getRequiredProperty("jdbc.user"));
        ds.setPassword(env.getRequiredProperty("jdbc.password"));
        ds.setDriverClassName(env.getRequiredProperty("jdbc.driver"));
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        Properties jpaProperties = new Properties();
        jpaProperties.setProperty("openjpa.Log", "slf4j");
        jpaProperties.setProperty("openjpa.ConnectionFactoryProperties", "PrettyPrint=true");
        LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
        emfBean.setDataSource(dataSource());
        emfBean.setJpaVendorAdapter(new OpenJpaVendorAdapter());
        emfBean.setJpaDialect(new OpenJpaDialect());
        emfBean.setPackagesToScan("com.gitlab.muhammadkholidb.bianglala.entity");
        emfBean.setJpaProperties(jpaProperties);
        return emfBean;
    }
    
    @Bean
    public JpaTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

    @Bean
    public SpringLiquibase springLiquibase() {
        SpringLiquibase springLiquibase = new SpringLiquibase();
        springLiquibase.setDataSource(dataSource());
        springLiquibase.setChangeLog("classpath:db/changelog/master.xml");
        return springLiquibase;
    }
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("searchProduct", "searchProductCategory", "searchProductCategoryByKeyword");
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }
    
}
