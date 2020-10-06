package com.gitlab.muhammadkholidb.bianglala.service;

import com.gitlab.muhammadkholidb.bianglala.entity.LanguageEntity;
import java.util.ArrayList;
import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.entity.ProductCategoryEntity;
import com.gitlab.muhammadkholidb.bianglala.repository.ProductCategoryRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductCategorySearchResult;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;

@Slf4j
@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Cacheable("searchProductCategoryByKeyword")
    public List<ProductCategorySearchResult> searchProductCategoryByKeyword(String keyword) {
        log.debug("Search product category");
        LanguageEntity language = new LanguageEntity();
        language.setId(2L);
        List<ProductCategoryEntity> categories = productCategoryRepository.findByKeyword(keyword, language, new PageRequest(0, 10));
        List<ProductCategorySearchResult> results = new ArrayList<>();
        int maxParent = 3;
        categories.stream().map(category -> {
            ProductCategoryEntity parentCategory = category.getParentCategory();
            int countParent = 0;
            while (parentCategory != null) {
                parentCategory = productCategoryRepository.findOne(parentCategory.getId());
                countParent++;
                category.setName(parentCategory.getName() + " > " + category.getName());
                parentCategory = parentCategory.getParentCategory();
                if (parentCategory != null && countParent == maxParent) {
                    category.setName("... > " + parentCategory.getName() + " > " + category.getName());
                    parentCategory = null;
                }
            }
            return category;
        }).map(category -> {
            ProductCategorySearchResult result = new ProductCategorySearchResult();
            BeanUtils.copyProperties(category, result);
            return result;
        }).forEachOrdered(result -> {
            results.add(result);
        });
        return results;
    }

}
