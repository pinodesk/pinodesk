package pospino.desktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static pospino.desktop.constant.ConfigurationConstants.LANGUAGE;

import java.util.Optional;

import com.github.springtestdbunit.annotation.DatabaseSetup;

import pospino.desktop.domain.Configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DatabaseSetup("ConfigurationRepositoryTest.xml")
class ConfigurationRepositoryTest extends RepositoryTestBase {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Test
    void testUpdateConfigurationByCode_shouldSucceed() {
        Integer rowsAffected = configurationRepository.updateValueByCode(LANGUAGE, "id");
        assertEquals(1, rowsAffected.intValue());
        Optional<Configuration> configuration = configurationRepository.findByCodeAndDeletedAtIsNull(LANGUAGE);
        assertThat(configuration.isPresent(), is(true));
        assertThat(
                configuration.get(),
                allOf(
                        hasProperty(Configuration.C_ID, is(2l)),
                        hasProperty(Configuration.C_CODE, is(LANGUAGE)),
                        hasProperty(Configuration.C_VALUE, is("id"))));
    }

}
