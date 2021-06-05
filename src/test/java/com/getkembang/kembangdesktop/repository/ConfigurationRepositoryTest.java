package com.getkembang.kembangdesktop.repository;

import static com.getkembang.kembangdesktop.constant.ConfigurationConstants.LANGUAGE_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;

import java.util.Optional;

import com.getkembang.kembangdesktop.domain.Configuration;
import com.github.database.rider.core.api.dataset.DataSet;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DataSet("t_configuration.yml")
class ConfigurationRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Test
    void testUpdateConfigurationByCode_shouldSucceed() {
        Integer rowsAffected = configurationRepository.updateConfigurationByCode(LANGUAGE_ID, "2");
        assertEquals(1, rowsAffected.intValue());
        Optional<Configuration> configuration = configurationRepository
                .readOne(new Where().equals(Configuration.C_CODE, LANGUAGE_ID));
        assertThat(configuration.isPresent(), Matchers.is(true));
        assertThat(configuration.get(),
                allOf(hasProperty(Configuration.C_ID, Matchers.is(1l)),
                        hasProperty(Configuration.C_CODE, Matchers.is(LANGUAGE_ID)),
                        hasProperty(Configuration.C_VALUE, Matchers.is("2"))));
    }

}
