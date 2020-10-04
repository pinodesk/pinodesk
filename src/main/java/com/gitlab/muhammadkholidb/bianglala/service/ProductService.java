package com.gitlab.muhammadkholidb.bianglala.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.muhammadkholidb.bianglala.entity.LanguageEntity;
import com.gitlab.muhammadkholidb.bianglala.entity.ProductCategoryEntity;
import com.gitlab.muhammadkholidb.bianglala.entity.ProductEntity;
import com.gitlab.muhammadkholidb.bianglala.entity.RackEntity;
import com.gitlab.muhammadkholidb.bianglala.entity.UnitEntity;
import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.repository.ProductRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilter;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductSearchResult;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Cacheable("searchProduct")
    public List<ProductSearchResult> filterProducts(ProductFilter param) {
        log.debug("Filter products");
        LanguageEntity language = new LanguageEntity();
        language.setId(2L);
        List<ProductEntity> products = productRepository.filter(param, language);
        return products.stream().map(product -> {
            ProductSearchResult result = objectMapper.convertValue(product, ProductSearchResult.class);
            ProductCategoryEntity pc = product.getCategory();
            UnitEntity unit = product.getUnit();
            RackEntity rack = product.getRack();
            if (pc != null) {
                result.setCategoryId(pc.getId());
                result.setCategoryCode(pc.getCode());
                result.setCategoryName(pc.getName());
            }
            if (unit != null) {
                result.setUnitId(unit.getId());
            }
            if (rack != null) {
                result.setRackId(rack.getId());
            }
            return result;
        }).collect(Collectors.toList());
    }

}
