package pinus.desktop.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.DrugCategory;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.DrugCategoryRepository;
import pinus.desktop.viewmodel.DrugCategoryVM;

class DrugCategoryServiceTest extends BaseServiceTest {

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private DrugCategoryRepository drugCategoryRepository;

    @InjectMocks
    private DrugCategoryService drugCategoryService;

    private DrugCategory drugCategory;

    @BeforeEach
    void setUp() {
        drugCategory = new DrugCategory();
        drugCategory.setId(1L);
        drugCategory.setDrugCategoryBaseId(1L);
        drugCategory.setCode("PERMENKESRI01");
        drugCategory.setName("Obat Bebas Terbatas");
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(configurationService, drugCategoryRepository);
    }

    @Test
    void testSearchDrugCategoriesByKeyword_shouldSucceed() {
        when(configurationService.getConfiguration(anyString())).thenReturn("1");
        when(drugCategoryRepository.findByKeyword(anyString(), anyLong()))
                .thenReturn(Collections.singletonList(drugCategory));
        List<DrugCategoryVM> results = drugCategoryService.searchDrugCategoriesByKeyword("keyword");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertDrugCategory(drugCategory, results.get(0));
        verify(configurationService).getConfiguration(anyString());
        verify(drugCategoryRepository).findByKeyword(anyString(), anyLong());
    }

    @Test
    void testGetDrugCategoryById_shouldSucceed() {
        when(drugCategoryRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.of(drugCategory));
        DrugCategoryVM result = drugCategoryService.getDrugCategoryById(1L);
        assertDrugCategory(drugCategory, result);
        verify(drugCategoryRepository).findByIdAndDeletedAtIsNull(anyLong());
    }

    @Test
    void testGetDrugCategoryById_idNotFound_shouldThrowDomainException() {
        when(drugCategoryRepository.findByIdAndDeletedAtIsNull(anyLong())).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> drugCategoryService.getDrugCategoryById(11L));
        assertEquals(DomainError.DRUG_CATEGORY_NOT_FOUND_BY_ID, ex.getError());
        verify(drugCategoryRepository).findByIdAndDeletedAtIsNull(anyLong());
    }

    private void assertDrugCategory(DrugCategory expected, DrugCategoryVM actual) {
        assertNotNull(actual);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
    }

}
