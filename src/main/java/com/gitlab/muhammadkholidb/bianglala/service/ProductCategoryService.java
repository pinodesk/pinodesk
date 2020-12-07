package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.ArrayList;
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

    public ProductCategoryVM getProductCateoryById(Long id) {
        return convertOptionalOrThrow(productCategoryRepository.readOne(id), ProductCategoryVM.class,
                new DomainException(DomainError.NOT_FOUND));
    }

    @Cacheable("searchProductCategoryByKeyword")
    public List<ProductCategoryVM> searchProductCategoryByKeyword(String keyword) {
        String languageId = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_ID);
        List<ProductCategory> categories = productCategoryRepository.filter(keyword, Long.valueOf(languageId));
        List<ProductCategoryVM> results = new ArrayList<>();
        int maxParent = 3;
        categories.stream().map(category -> {
            Long parentCategoryId = category.getParentCategoryId();
            int countParent = 0;
            while (parentCategoryId != null) {
                ProductCategory parentCategory = productCategoryRepository.readOne(parentCategoryId).orElseThrow();
                countParent++;
                category.setName(parentCategory.getName() + " > " + category.getName());
                parentCategoryId = parentCategory.getParentCategoryId();
                if (parentCategoryId != null && countParent == maxParent) {
                    category.setName("... > " + parentCategory.getName() + " > " + category.getName());
                    parentCategoryId = null;
                }
            }
            return category;
        }).map(category -> convertObject(category, ProductCategoryVM.class)).forEachOrdered(results::add);
        return results;
    }

}
