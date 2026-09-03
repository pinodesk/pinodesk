package com.pinodesk.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.constant.Activity;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.ConfigurationConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.entity.ProductCategory;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.ProductCategoryRepository;
import com.pinodesk.viewmodel.ProductCategoryVM;

@Service
public class ProductCategoryService extends BaseService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ConfigurationService configurationService;

    @TargetActivity(Activity.GET_PRODUCT_CATEGORY_BY_ID)
    public ProductCategoryVM getProductCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                productCategoryRepository.findByIdAndDeletedAtIsNull(id),
                ProductCategoryVM.class,
                new DomainException(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_ID));
    }

    @TargetActivity(Activity.SEARCH_PRODUCT_CATEGORIES_BY_KEYWORD)
    @Cacheable(CacheNameConstants.PRODUCT_CATEGORIES_BY_KEYWORD)
    public List<ProductCategoryVM> searchProductCategoryByKeyword(String keyword) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        List<ProductCategory> categories;
        if (StringUtils.isBlank(keyword)) {
            categories = productCategoryRepository.findByLanguageAndDeletedAtIsNullOrderByName(language);
        } else {
            categories = productCategoryRepository.findByKeyword(keyword, language);
        }
        return objectConverter.convertList(categories, ProductCategoryVM.class);
    }

    public ProductCategoryVM getProductCategoryByCode(String code, String language) {
        return objectConverter.convertOptionalOrThrow(
                productCategoryRepository.findByLanguageAndCodeAndDeletedAtIsNull(language, code),
                ProductCategoryVM.class,
                new DomainException(DomainError.PRODUCT_CATEGORY_NOT_FOUND_BY_CODE));
    }

}
