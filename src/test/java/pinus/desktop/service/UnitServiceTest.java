package pinus.desktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.Unit;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.UnitRepository;
import pinus.desktop.viewmodel.UnitVM;

class UnitServiceTest extends BaseServiceTest {

    @Mock
    private UnitRepository unitRepository;

    @InjectMocks
    private UnitService unitService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(unitRepository);
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
        when(unitRepository.findByKeyword(anyString(), anyInt())).thenReturn(new ArrayList<>());
        List<UnitVM> units = unitService.searchUnitByKeyword("keyword");
        assertNotNull(units);
        assertEquals(0, units.size());
        verify(unitRepository).findByKeyword(anyString(), anyInt());
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
        assertEquals(DomainError.UNIT_NOT_FOUND_BY_ID, ex.getError());
        verify(unitRepository).findByIdAndDeletedAtIsNull(anyLong());
    }

}
