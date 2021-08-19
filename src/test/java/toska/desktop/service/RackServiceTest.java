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
import toska.desktop.domain.Rack;
import toska.desktop.exception.DomainException;
import toska.desktop.repository.RackRepository;
import toska.desktop.viewmodel.RackVM;

class RackServiceTest extends BaseServiceTest {

    @Mock
    private RackRepository rackRepository;

    @InjectMocks
    private RackService rackService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(rackRepository);
    }

    @Test
    void testGetAllRacks_shouldSucceed() {
        when(rackRepository.read()).thenReturn(new ArrayList<>());
        List<RackVM> racks = rackService.getAllRacks();
        assertNotNull(racks);
        assertEquals(0, racks.size());
        verify(rackRepository).read();
    }

    @Test
    void testSearchRackByKeyword_shouldSucceed() {
        when(rackRepository.filter(anyString(), anyInt())).thenReturn(new ArrayList<>());
        List<RackVM> racks = rackService.searchRackByKeyword("keyword");
        assertNotNull(racks);
        assertEquals(0, racks.size());
        verify(rackRepository).filter(anyString(), anyInt());
    }

    @Test
    void testGetRackById_shouldSucceed() {
        Rack rack = new Rack();
        rack.setId(1L);
        when(rackRepository.readOne(anyLong())).thenReturn(Optional.of(rack));
        RackVM result = rackService.getRackById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId().longValue());
        verify(rackRepository).readOne(anyLong());
    }

    @Test
    void testGetRackById_idNotFound_shouldThrowDomainException() {
        when(rackRepository.readOne(anyLong())).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> rackService.getRackById(1L));
        assertEquals(DomainError.RACK_NOT_FOUND_BY_ID, ex.getError());
        verify(rackRepository).readOne(anyLong());
    }

}
