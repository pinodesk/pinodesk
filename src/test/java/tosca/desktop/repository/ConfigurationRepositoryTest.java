package tosca.desktop.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tosca.desktop.constant.ConfigurationConstants.LANGUAGE_CODE;

import java.util.Optional;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import tosca.desktop.domain.Configuration;

@DatabaseSetup("ConfigurationRepositoryTest.xml")
class ConfigurationRepositoryTest extends RepositoryTestBase {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Test
    void testUpdateConfigurationByCode_shouldSucceed() {
        Integer rowsAffected = configurationRepository.updateConfigurationByCode(LANGUAGE_CODE, "id");
        assertEquals(1, rowsAffected.intValue());
        Optional<Configuration> configuration = configurationRepository
                .readOne(new Where().equals(Configuration.C_CODE, LANGUAGE_CODE));
        assertThat(configuration.isPresent(), is(true));
        assertThat(configuration.get(), allOf(hasProperty(Configuration.C_ID, is(2l)),
                hasProperty(Configuration.C_CODE, is(LANGUAGE_CODE)), hasProperty(Configuration.C_VALUE, is("id"))));
    }

}
