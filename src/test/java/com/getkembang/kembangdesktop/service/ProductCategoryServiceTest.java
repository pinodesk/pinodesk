
package com.getkembang.kembangdesktop.service;

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

import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.domain.ProductCategory;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.ProductCategoryRepository;
import com.getkembang.kembangdesktop.viewmodel.ProductCategoryVM;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

class ProductCategoryServiceTest extends BaseServiceTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private ProductCategoryService productCategoryService;

    private ProductCategory productCategory;

    @BeforeEach
    void setUp() {
        productCategory = new ProductCategory();
        productCategory.setId(1L);
        productCategory.setCode("0001");
        productCategory.setLanguageCode("en");
        productCategory.setName("Category 0001");
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(configurationService, productCategoryRepository);
    }

    @Test
    void testSearchProductCategoryByKeyword_shouldSucceed() {
        when(configurationService.getConfiguration(anyString())).thenReturn("1");
        when(productCategoryRepository.filter(anyString(), anyString())).thenReturn(new ArrayList<>());
        List<ProductCategoryVM> results = productCategoryService.searchProductCategoryByKeyword("keyword");
        assertNotNull(results);
        assertEquals(0, results.size());
        verify(productCategoryRepository).filter(anyString(), anyString());
    }

    @Test
    void testGetProductCategoryById_shouldSucceed() {
        when(productCategoryRepository.readOne(anyLong())).thenReturn(Optional.of(productCategory));
        ProductCategoryVM result = productCategoryService.getProductCategoryById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId().longValue());
        verify(productCategoryRepository).readOne(anyLong());
    }

    @Test
    void testGetProductCategoryById_idNotFound_shouldThrowDomainException() {
        when(productCategoryRepository.readOne(anyLong())).thenReturn(Optional.empty());
        DomainException ex = assertThrows(DomainException.class, () -> productCategoryService.getProductCategoryById(1L));
        assertEquals(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_ID, ex.getError());
        verify(productCategoryRepository).readOne(anyLong());
    }

}
