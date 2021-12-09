package pinus.desktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import pinus.desktop.repository.WholesaleRepository;
import pinus.desktop.viewmodel.WholesaleVM;

class WholesaleServiceTest extends BaseServiceTest {

    @Mock
    private WholesaleRepository wholesaleRepository;

    @InjectMocks
    private WholesaleService wholesaleService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(wholesaleRepository);
    }

    @Test
    void testGetWholesalesByProductId_shouldSucceed() {
        when(wholesaleRepository.read(any(Where.class), any(Order.class))).thenReturn(new ArrayList<>());
        List<WholesaleVM> wholesales = wholesaleService.getWholesalesByProductId(1L);
        assertNotNull(wholesales);
        assertEquals(0, wholesales.size());
        verify(wholesaleRepository).read(any(Where.class), any(Order.class));
    }

}
