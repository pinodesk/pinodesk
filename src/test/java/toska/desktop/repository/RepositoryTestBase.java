package toska.desktop.repository;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.gitlab.muhammadkholidb.sequel.config.SequelConfig;

import org.apache.commons.dbcp2.BasicDataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.h2.H2DataTypeFactory;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@TestExecutionListeners({ DbUnitTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection = "databaseConnection")
@SpringJUnitConfig(RepositoryTestBase.Config.class)
public abstract class RepositoryTestBase {

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

        @Bean
        public IDatabaseConnection databaseConnection(DataSource dataSource, DatabaseMetaData databaseMetaData)
                throws SQLException, DatabaseUnitException {
            DatabaseDataSourceConnection databaseConnection = new DatabaseDataSourceConnection(dataSource);
            DatabaseConfig databaseConfig = databaseConnection.getConfig();
            databaseConfig.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            databaseConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true);
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "\"?\"");
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
            return databaseConnection;
        }

    }

}
