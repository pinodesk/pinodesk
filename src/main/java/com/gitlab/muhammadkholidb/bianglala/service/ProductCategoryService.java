package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.ArrayList;
import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;
import com.gitlab.muhammadkholidb.bianglala.data.model.ProductCategory;
import com.gitlab.muhammadkholidb.bianglala.data.repository.ProductCategoryRepository;
import com.gitlab.muhammadkholidb.bianglala.utility.ConfigurationHolder;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategorySearchResult;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Cacheable("searchProductCategoryByKeyword")
    public List<ProductCategorySearchResult> searchProductCategoryByKeyword(String keyword) {
        log.debug("Search product category");
        String languageId = ConfigurationHolder.getConfiguration(ConfigurationConstants.LANGUAGE_ID);
        List<ProductCategory> categories = productCategoryRepository.filter(keyword, Long.valueOf(languageId));
        List<ProductCategorySearchResult> results = new ArrayList<>();
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
        }).map(category -> {
            ProductCategorySearchResult result = new ProductCategorySearchResult();
            BeanUtils.copyProperties(category, result);
            return result;
        }).forEachOrdered(results::add);
        return results;
    }

}
