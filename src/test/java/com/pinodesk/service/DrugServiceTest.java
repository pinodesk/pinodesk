package com.pinodesk.service;

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

import com.pinodesk.entity.Drug;
import com.pinodesk.repository.DrugRepository;
import com.pinodesk.viewmodel.DrugVM;

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
    void absentDrugReturnsNullForNonDrugProduct() {
        when(drugRepository.findByProductIdAndDeletedAtIsNull(77L)).thenReturn(Optional.empty());
        org.junit.jupiter.api.Assertions.assertNull(drugService.getDrugByProductId(77L));
        verify(drugRepository).findByProductIdAndDeletedAtIsNull(77L);
    }

    @Test
    void lookupPreservesDrugClassificationAndUsageDetails() {
        drug.setProductId(77L);
        drug.setClassificationCode("RX");
        drug.setIndication("Indication text");
        drug.setContraindication("Contraindication text");
        when(drugRepository.findByProductIdAndDeletedAtIsNull(77L)).thenReturn(Optional.of(drug));
        DrugVM result = drugService.getDrugByProductId(77L);
        assertEquals(77L, result.getProductId());
        assertEquals("RX", result.getClassificationCode());
        assertEquals("Indication text", result.getIndication());
        assertEquals("Contraindication text", result.getContraindication());
        verify(drugRepository).findByProductIdAndDeletedAtIsNull(77L);
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
