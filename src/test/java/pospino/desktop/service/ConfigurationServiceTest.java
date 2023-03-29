package pospino.desktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import pospino.desktop.domain.Configuration;
import pospino.desktop.repository.ConfigurationRepository;

class ConfigurationServiceTest extends BaseServiceTest {

    @Mock
    private ConfigurationRepository configurationRepository;

    @InjectMocks
    private ConfigurationService configurationService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(configurationRepository);
    }

    @Test
    void testGetConfiguration_shouldReturnValue() {
        Configuration configuration = new Configuration();
        configuration.setValue("value");
        when(configurationRepository.findByCodeAndDeletedAtIsNull(anyString())).thenReturn(Optional.of(configuration));
        String value = configurationService.getConfiguration("code");
        assertNotNull(value);
        assertEquals("value", value);
        verify(configurationRepository).findByCodeAndDeletedAtIsNull(anyString());
    }

    @Test
    void testGetConfiguration_notFound_shouldReturnNull() {
        when(configurationRepository.findByCodeAndDeletedAtIsNull(anyString())).thenReturn(Optional.empty());
        String value = configurationService.getConfiguration("code");
        assertNull(value);
        verify(configurationRepository).findByCodeAndDeletedAtIsNull(anyString());
    }

}
