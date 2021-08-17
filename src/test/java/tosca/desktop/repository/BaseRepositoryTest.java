package tosca.desktop.repository;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import com.gitlab.muhammadkholidb.sequel.config.SequelConfig;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@DBRider
@DBUnit(caseSensitiveTableNames = true)
@SpringJUnitConfig(BaseRepositoryTest.Config.class)
public abstract class BaseRepositoryTest {

    @Configuration
    @Import(SequelConfig.class)
    @ComponentScan
    @EnableTransactionManagement
    @PropertySource({ "classpath:application-test.properties" })
    static class Config {

        @Resource // https://stackoverflow.com/questions/19421092/autowired-environment-is-null
        private Environment env;

        @Bean
        public DataSource dataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(env.getRequiredProperty("jdbc.driver"));
            dataSource.setUrl(env.getRequiredProperty("jdbc.url"));
            dataSource.setUsername(env.getRequiredProperty("jdbc.user"));
            dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
            dataSource.setInitialSize(10);
            dataSource.setMaxTotal(10);

            DatabasePopulator databasePopulator = new ResourceDatabasePopulator(new ClassPathResource("init.sql"));
            DatabasePopulatorUtils.execute(databasePopulator, dataSource);

            return dataSource;
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

    }

}
