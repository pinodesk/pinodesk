package pinodesk.repository;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcAuditing;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.MySqlDialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.mudiatech.sequel.config.SequelConfig;

@TestExecutionListeners({ DbUnitTestExecutionListener.class, DependencyInjectionTestExecutionListener.class })
@DbUnitConfiguration(databaseConnection = "databaseConnection")
@SpringJUnitConfig(RepositoryTestBase.Config.class)
public abstract class RepositoryTestBase {

    @Configuration
    @Import(SequelConfig.class)
    @ComponentScan
    @EnableTransactionManagement
    @EnableJdbcRepositories
    @EnableJdbcAuditing
    @PropertySource({ "classpath:application-test.properties" })
    static class Config extends AbstractJdbcConfiguration {

        @Autowired
        private Environment env;

        @Bean
        public DataSource dataSource() {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setDriverClassName(env.getRequiredProperty("jdbc.driver"));
            dataSource.setJdbcUrl(env.getRequiredProperty("jdbc.url"));
            dataSource.setUsername(env.getRequiredProperty("jdbc.user"));
            dataSource.setPassword(env.getRequiredProperty("jdbc.password"));
            dataSource.setMaximumPoolSize(10);
            dataSource.setMinimumIdle(10);
            DatabasePopulator databasePopulator = new ResourceDatabasePopulator(new ClassPathResource("init.sql"));
            DatabasePopulatorUtils.execute(databasePopulator, dataSource);
            return dataSource;
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public IDatabaseConnection databaseConnection(DataSource dataSource) throws SQLException,
                DatabaseUnitException {
            DatabaseDataSourceConnection databaseConnection = new DatabaseDataSourceConnection(dataSource);
            DatabaseConfig databaseConfig = databaseConnection.getConfig();
            databaseConfig.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true);
            databaseConfig.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true);
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "\"?\"");
            databaseConfig.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
            return databaseConnection;
        }

        @Override
        public Dialect jdbcDialect(NamedParameterJdbcOperations operations) {
            return MySqlDialect.INSTANCE;
        }

    }

}
