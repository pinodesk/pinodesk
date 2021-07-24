package com.getkembang.kembangdesktop.service;

import java.util.List;

import com.getkembang.kembangdesktop.constant.CacheName;
import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.domain.ProductCategory;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.ProductCategoryRepository;
import com.getkembang.kembangdesktop.viewmodel.ProductCategoryVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductCategoryService extends BaseService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ConfigurationService configurationService;

    public ProductCategoryVM getProductCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(productCategoryRepository.readOne(id), ProductCategoryVM.class,
                new DomainException(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_ID));
    }

    @Cacheable(CacheName.Keys.PRODUCT_CATEGORIES_BY_KEYWORD)
    public List<ProductCategoryVM> searchProductCategoryByKeyword(String keyword) {
        String languageCode = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_CODE);
        List<ProductCategory> categories = productCategoryRepository.filter(keyword, languageCode);
        return objectConverter.convertList(categories, ProductCategoryVM.class);
    }

}
