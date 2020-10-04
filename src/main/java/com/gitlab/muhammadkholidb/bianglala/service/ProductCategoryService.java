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
        int maxNested = 3;
        for (ProductCategoryEntity category : categories) {
            ProductCategoryEntity parentCategory = category.getParentCategory();
            int countNested = 0;
            while (parentCategory != null) {
                parentCategory = productCategoryRepository.findOne(parentCategory.getId());
                if (parentCategory != null) {
                    if (countNested == (maxNested - 1)) {
                        category.setName("... > " + parentCategory.getName() + " > " + category.getName());
                        parentCategory = null;
                        continue;
                    }
                    category.setName(parentCategory.getName() + " > " + category.getName());
                }
                countNested++;
            }
            ProductCategorySearchResult result = new ProductCategorySearchResult();
            BeanUtils.copyProperties(category, result);
            results.add(result);
        }
        return results;
    }

}
