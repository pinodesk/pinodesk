package pinus.desktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pinus.desktop.constant.ConfigurationConstants.LANGUAGE_CODE;

import java.util.Optional;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pinus.desktop.domain.Configuration;

@DatabaseSetup("ConfigurationRepositoryTest.xml")
class ConfigurationRepositoryTest extends RepositoryTestBase {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Test
    void testUpdateConfigurationByCode_shouldSucceed() {
        Integer rowsAffected = configurationRepository.updateValueByCode(LANGUAGE_CODE, "id");
        assertEquals(1, rowsAffected.intValue());
        Optional<Configuration> configuration = configurationRepository.findByCodeAndDeletedAtIsNull(LANGUAGE_CODE);
        assertThat(configuration.isPresent(), is(true));
        assertThat(
                configuration.get(),
                allOf(
                        hasProperty(Configuration.C_ID, is(2l)),
                        hasProperty(Configuration.C_CODE, is(LANGUAGE_CODE)),
                        hasProperty(Configuration.C_VALUE, is("id"))));
    }

}
