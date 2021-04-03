package com.getkembang.kembangdesktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import com.getkembang.kembangdesktop.domain.Drug;
import com.getkembang.kembangdesktop.repository.DrugRepository;
import com.getkembang.kembangdesktop.viewmodel.DrugVM;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
        when(drugRepository.readOne(any(Where.class))).thenReturn(Optional.of(drug));
        DrugVM result = drugService.getDrugByProductId(1L);
        assertNotNull(result);
        assertEquals(drug.getId(), result.getId());
        verify(drugRepository).readOne(any(Where.class));
    }

}
