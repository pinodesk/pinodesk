package stoready.desktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import stoready.desktop.domain.Drug;
import stoready.desktop.repository.DrugRepository;
import stoready.desktop.viewmodel.DrugVM;

class DrugServiceTest extends BaseServiceTest {

    @Mock
    private DrugRepository drugRepository;

    @InjectMocks
    private DrugService drugService;

    private Drug drug;

    @BeforeEach
    void setUp() {
        drug = new Drug();
        drug.setId(1L);
        drug.setProductId(1L);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(drugRepository);
    }

    @Test
    void testGetDrugByProductId_shouldSucceed() {
        when(drugRepository.findByProductIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(drug));
        DrugVM result = drugService.getDrugByProductId(1L);
        assertNotNull(result);
        assertEquals(drug.getId(), result.getId());
        verify(drugRepository).findByProductIdAndDeletedAtIsNull(anyLong());
    }

}
