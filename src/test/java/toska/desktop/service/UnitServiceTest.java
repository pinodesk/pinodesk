package toska.desktop.service;

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

import toska.desktop.constant.DomainError;
import toska.desktop.domain.Unit;
import toska.desktop.exception.DomainException;
import toska.desktop.repository.UnitRepository;
import toska.desktop.viewmodel.UnitVM;

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
        when(unitRepository.read()).thenReturn(new ArrayList<>());
        List<UnitVM> units = unitService.getAllUnits();
        assertNotNull(units);
        assertEquals(0, units.size());
        verify(unitRepository).read();
    }

    @Test
    void testSearchUnitByKeyword_shouldSucceed() {
        when(unitRepository.filter(anyString(), anyInt())).thenReturn(new ArrayList<>());
        List<UnitVM> units = unitService.searchUnitByKeyword("keyword");
        assertNotNull(units);
        assertEquals(0, units.size());
        verify(unitRepository).filter(anyString(), anyInt());
    }

    @Test
    void testGetUnitById_shouldSucceed() {
        Unit unit = new Unit();
        unit.setId(1L);
        when(unitRepository.readOne(anyLong())).thenReturn(Optional.of(unit));
        UnitVM result = unitService.getUnitById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId().longValue());
        verify(unitRepository).readOne(anyLong());
    }

    @Test
    void testGetUnitById_idNotFound_shouldThrowDomainException() {
        when(unitRepository.readOne(anyLong())).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> unitService.getUnitById(1L));
        assertEquals(DomainError.UNIT_NOT_FOUND_BY_ID, ex.getError());
        verify(unitRepository).readOne(anyLong());
    }

}
