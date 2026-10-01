package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.Unit;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.UnitRepository;
import com.pinodesk.viewmodel.UnitVM;

class UnitServiceTest extends BaseServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private UnitService unitService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(unitRepository, configurationService);
    }

    @Test
    void codeLookupPreservesUnitDetailsAndLanguage() {
        Unit unit = new Unit("Tablet", "tab", "TABLET", "id");
        unit.setId(42L);
        when(unitRepository.findByLanguageAndCodeAndDeletedAtIsNull("id", "TABLET")).thenReturn(Optional.of(unit));
        UnitVM result = unitService.getUnitByCode("TABLET", "id");
        assertEquals(42L, result.getId());
        assertEquals("Tablet", result.getName());
        assertEquals("tab", result.getLabel());
        assertEquals("TABLET", result.getCode());
        verify(unitRepository).findByLanguageAndCodeAndDeletedAtIsNull("id", "TABLET");
    }

    @Test
    void missingCodeReportsSpecificDomainError() {
        when(unitRepository.findByLanguageAndCodeAndDeletedAtIsNull("en", "missing")).thenReturn(Optional.empty());
        DomainException failure = assertThrows(DomainException.class, () -> unitService.getUnitByCode("missing", "en"));
        assertEquals(DomainError.UNIT_NOT_FOUND_BY_CODE, failure.getError());
        verify(unitRepository).findByLanguageAndCodeAndDeletedAtIsNull("en", "missing");
    }

    @Test
    void blankSearchUsesConfiguredLanguageAndMapsResults() {
        Unit unit = new Unit("Tablet", "tab", "TABLET", "id");
        when(configurationService.getConfiguration("language")).thenReturn("id");
        when(unitRepository.findByLanguageAndDeletedAtIsNullOrderByName("id")).thenReturn(List.of(unit));
        for (String keyword : new String[] { null, "", "   " }) {
            List<UnitVM> results = unitService.searchUnitByKeyword(keyword);
            assertEquals(1, results.size());
            assertEquals("Tablet", results.get(0).getName());
            assertEquals("TABLET", results.get(0).getCode());
        }
        org.mockito.Mockito.verify(configurationService, org.mockito.Mockito.times(3)).getConfiguration("language");
        org.mockito.Mockito.verify(unitRepository, org.mockito.Mockito.times(3))
                .findByLanguageAndDeletedAtIsNullOrderByName("id");
    }

    @Test
    void failedLanguageLookupDoesNotQueryUnits() {
        IllegalStateException failure = new IllegalStateException("configuration unavailable");
        when(configurationService.getConfiguration("language")).thenThrow(failure);
        org.junit.jupiter.api.Assertions.assertSame(
                failure,
                assertThrows(IllegalStateException.class, () -> unitService.searchUnitByKeyword("tablet")));
        verify(configurationService).getConfiguration("language");
    }

    @Test
    void testGetAllUnits_shouldSucceed() {
        when(unitRepository.findByDeletedAtIsNull()).thenReturn(new ArrayList<>());
        List<UnitVM> units = unitService.getAllUnits();
        assertNotNull(units);
        assertEquals(0, units.size());
        verify(unitRepository).findByDeletedAtIsNull();
    }

    @Test
    void testSearchUnitByKeyword_shouldSucceed() {
        when(configurationService.getConfiguration(anyString())).thenReturn("en");
        when(unitRepository.findByKeyword(anyString(), anyString())).thenReturn(new ArrayList<>());
        List<UnitVM> units = unitService.searchUnitByKeyword("keyword");
        assertNotNull(units);
        assertEquals(0, units.size());
        verify(configurationService).getConfiguration(anyString());
        verify(unitRepository).findByKeyword(anyString(), anyString());
    }

    @Test
    void testGetUnitById_shouldSucceed() {
        Unit unit = new Unit();
        unit.setId(1L);
        when(unitRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(unit));
        UnitVM result = unitService.getUnitById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId().longValue());
        verify(unitRepository).findByIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    void testGetUnitById_idNotFound_shouldThrowDomainException() {
        when(unitRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> unitService.getUnitById(1L));
        Assertions.assertEquals(DomainError.UNIT_NOT_FOUND_BY_ID, ex.getError());
        verify(unitRepository).findByIdAndDeletedAtIsNull(anyLong());
    }

}
