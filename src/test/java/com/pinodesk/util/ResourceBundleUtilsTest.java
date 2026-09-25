package com.pinodesk.util;

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

import com.pinodesk.properties.ApplicationProperties;
import com.pinodesk.repository.ConfigurationRepository;
import com.pinodesk.repository.UserRepository;
import com.pinodesk.service.ConfigurationService;
import com.pinodesk.toolbox.jackson.ObjectConverter;

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
        assertThat(rb.getLocale(), is(Locale.forLanguageTag("id")));
    }

    @Test
    void testInstallationRegistrationKeys_existInBothResourceBundles() {
        String[] keys = {
                "lbl_title_register_installation",
                "lbl_installation_description",
                "lbl_installation_code",
                "btn_request_code",
                "btn_register_now",
                "btn_skip_for_now",
                "lbl_installation_registration_info",
                "lbl_installation_registration_registered",
                "error_empty_email",
                "error_empty_installation_code",
                "error_empty_installation_request_id",
                "success_request_installation_code",
                "success_installation_registration" };
        ResourceBundle enBundle = ResourceBundle.getBundle("pinodesk.lang", Locale.ENGLISH);
        ResourceBundle idBundle = ResourceBundle.getBundle("pinodesk.lang", Locale.forLanguageTag("id"));

        for (String key : keys) {
            assertThat("Missing key in en bundle: " + key, enBundle.containsKey(key), is(true));
            assertThat("Missing key in id bundle: " + key, idBundle.containsKey(key), is(true));
        }
    }

    @Test
    void testRegisterInstallationFxml_allResourceKeysExist() throws Exception {
        java.io.InputStream is = getClass().getResourceAsStream("/assets/templates/register-installation.fxml");
        assertThat(is, is(notNullValue()));
        String fxmlContent = new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);

        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("%([a-zA-Z0-9_]+)");
        java.util.regex.Matcher matcher = pattern.matcher(fxmlContent);

        ResourceBundle enBundle = ResourceBundle.getBundle("pinodesk.lang", Locale.ENGLISH);
        ResourceBundle idBundle = ResourceBundle.getBundle("pinodesk.lang", Locale.forLanguageTag("id"));

        while (matcher.find()) {
            String key = matcher.group(1);
            assertThat("Missing key in en bundle: " + key, enBundle.containsKey(key), is(true));
            assertThat("Missing key in id bundle: " + key, idBundle.containsKey(key), is(true));
        }
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
        public ApplicationProperties applicationProperties() {
            return mock(ApplicationProperties.class);
        }

        @Bean
        public ConfigurationService configurationService() {
            return configurationService;
        }
    }

}
