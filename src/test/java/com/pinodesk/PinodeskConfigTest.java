package com.pinodesk;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.spring4.properties.EncryptablePropertySourcesPlaceholderConfigurer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

public class PinodeskConfigTest {

    @Test
    void testStringEncryptor_shouldEncryptAndDecrypt() {
        StringEncryptor encryptor = PinodeskConfig.stringEncryptor();

        String plaintext = "test";
        String encrypted = encryptor.encrypt(plaintext);
        String decrypted = encryptor.decrypt(encrypted);

        assertThat(encrypted, is(not(equalTo(plaintext))));
        assertThat(decrypted, is(equalTo(plaintext)));
    }

    @Test
    void testByteEncryptor_shouldEncryptAndDecrypt() {
        StandardPBEByteEncryptor encryptor = PinodeskConfig.byteEncryptor();

        byte[] plaintext = "test".getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = encryptor.encrypt(plaintext);
        byte[] decrypted = encryptor.decrypt(encrypted);

        assertThat(Arrays.equals(encrypted, plaintext), is(false));
        assertThat(Arrays.equals(decrypted, plaintext), is(true));
    }

    @Test
    void testPropertySourcesPlaceholderConfigurer_shouldDecryptEncryptedProperty() {
        StringEncryptor encryptor = PinodeskConfig.stringEncryptor();

        PropertySourcesPlaceholderConfigurer configurer = PinodeskConfig
                .propertySourcesPlaceholderConfigurer(encryptor);

        assertThat(configurer, is(instanceOf(EncryptablePropertySourcesPlaceholderConfigurer.class)));

        String plaintext = "test-secret";
        String encrypted = encryptor.encrypt(plaintext);

        Properties properties = new Properties();
        properties.setProperty("test.secret", "ENC(" + encrypted + ")");

        /*
         * The configurer created by PinodeskConfig points to application.properties.
         * This test supplies its own property and should not depend on that file being
         * available in the test runtime.
         */
        configurer.setIgnoreResourceNotFound(true);
        configurer.setProperties(properties);

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        RootBeanDefinition beanDefinition = new RootBeanDefinition(TestConfigBean.class);
        beanDefinition.getPropertyValues().add("secret", "${test.secret}");
        beanFactory.registerBeanDefinition("testConfigBean", beanDefinition);

        configurer.postProcessBeanFactory(beanFactory);

        TestConfigBean bean = beanFactory.getBean(TestConfigBean.class);

        assertThat(bean.getSecret(), is(equalTo(plaintext)));
    }

    public static class TestConfigBean {

        private String secret;

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
