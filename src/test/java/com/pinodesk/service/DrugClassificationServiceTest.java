package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.DrugClassification;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.DrugClassificationRepository;
import com.pinodesk.viewmodel.DrugClassificationVM;

class DrugClassificationServiceTest extends BaseServiceTest {
    @Mock
    private DrugClassificationRepository repository;
    @InjectMocks
    private DrugClassificationService service;

    private DrugClassification classification() {
        DrugClassification entity = new DrugClassification();
        entity.setId(5L);
        entity.setCode("RX");
        entity.setLanguage("id");
        entity.setName("Resep");
        entity.setDescription("Memerlukan resep");
        return entity;
    }

    @Test
    void lookupByIdMapsClassificationDetails() {
        when(repository.findByIdAndDeletedAtIsNull(5L)).thenReturn(Optional.of(classification()));
        DrugClassificationVM result = service.getDrugClassificationById(5L);
        assertEquals(5L, result.getId());
        assertEquals("RX", result.getCode());
        assertEquals("Resep", result.getName());
        assertEquals("Memerlukan resep", result.getDescription());
    }

    @Test
    void lookupByCodeRespectsLanguage() {
        when(repository.findByLanguageAndCodeAndDeletedAtIsNull("id", "RX")).thenReturn(Optional.of(classification()));
        assertEquals("Resep", service.getDrugClassificationByCode("RX", "id").getName());
        verify(repository).findByLanguageAndCodeAndDeletedAtIsNull("id", "RX");
    }

    @Test
    void absentIdAndCodeHaveDistinctErrors() {
        DomainException id = assertThrows(DomainException.class, () -> service.getDrugClassificationById(5L));
        DomainException code = assertThrows(
                DomainException.class,
                () -> service.getDrugClassificationByCode("RX", "en"));
        assertEquals(DomainError.DRUG_CLASSIFICATION_NOT_FOUND_BY_ID, id.getError());
        assertEquals(DomainError.DRUG_CLASSIFICATION_NOT_FOUND_BY_CODE, code.getError());
    }

    @Test
    void searchMapsRepositoryResultsAndHandlesNoMatches() {
        when(repository.findByKeyword("res", "id")).thenReturn(List.of(classification()));
        List<DrugClassificationVM> result = service.searchDrugClassificationsByKeyword("res", "id");
        assertEquals(1, result.size());
        assertEquals("RX", result.get(0).getCode());
        assertTrue(service.searchDrugClassificationsByKeyword("absent", "en").isEmpty());
        verify(repository).findByKeyword("absent", "en");
    }

    @Test
    void repositoryFailureIsNotMisreportedAsMissingClassification() {
        IllegalStateException failure = new IllegalStateException("database unavailable");
        when(repository.findByIdAndDeletedAtIsNull(5L)).thenThrow(failure);
        assertSame(failure, assertThrows(IllegalStateException.class, () -> service.getDrugClassificationById(5L)));
    }
}
