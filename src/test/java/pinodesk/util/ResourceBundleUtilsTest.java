package pinodesk.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;
import javax.validation.Validator;

import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.gitlab.mudiasoft.toolbox.jackson.ObjectConverter;

import pinodesk.repository.ConfigurationRepository;
import pinodesk.repository.UserRepository;
import pinodesk.service.ConfigurationService;

@MockitoSettings(strictness = Strictness.WARN)
@ExtendWith(MockitoExtension.class)
public class ResourceBundleUtilsTest {

    @Mock
    private static ConfigurationService configurationService;

    @Test
    void testGetDefaultResourceBundle_shouldSucceed() {
        ResourceBundle rb = ResourceBundleUtils.getDefaultResourceBundle();
        assertThat(rb, is(notNullValue()));
        assertThat(rb.getLocale(), is(Locale.ENGLISH));
        when(configurationService.getConfiguration(anyString())).thenReturn("id");
        SpringUtils.init(SampleConfig.class);
        rb = ResourceBundleUtils.getDefaultResourceBundle();
        assertThat(rb, is(notNullValue()));
        assertThat(rb.getLocale(), is(new Locale("id")));
    }

    @Configuration
    public static class SampleConfig {

        @Bean
        public ObjectConverter objectConverter() {
            return mock(ObjectConverter.class);
        }

        @Bean
        public Validator validator() {
            return mock(Validator.class);
        }

        @Bean
        public CacheManager cacheManager() {
            return mock(CacheManager.class);
        }

        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        public ConfigurationRepository configurationRepository() {
            return mock(ConfigurationRepository.class);
        }

        @Bean
        public JdbcTemplate jdbcTemplate() {
            return mock(JdbcTemplate.class);
        }

        @Bean
        public DataSource dataSource() {
            return mock(DataSource.class);
        }

        @Bean
        public static StandardPBEByteEncryptor byteEncryptor() {
            return mock(StandardPBEByteEncryptor.class);
        }

        @Bean
        public ConfigurationService configurationService() {
            return configurationService;
        }
    }

}
