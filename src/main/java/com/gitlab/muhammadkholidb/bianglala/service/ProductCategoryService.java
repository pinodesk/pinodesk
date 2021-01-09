package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.DomainError;
import com.gitlab.muhammadkholidb.bianglala.domain.ProductCategory;
import com.gitlab.muhammadkholidb.bianglala.exception.DomainException;
import com.gitlab.muhammadkholidb.bianglala.repository.ProductCategoryRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategoryVM;

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
        return convertOptionalOrThrow(productCategoryRepository.readOne(id), ProductCategoryVM.class,
                new DomainException(DomainError.NOT_FOUND));
    }

    @Cacheable("productCategoriesByKeyword")
    public List<ProductCategoryVM> searchProductCategoryByKeyword(String keyword) {
        String languageId = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_ID);
        List<ProductCategory> categories = productCategoryRepository.filter(keyword, Long.valueOf(languageId));
        return convertList(categories, ProductCategoryVM.class);
    }

}
