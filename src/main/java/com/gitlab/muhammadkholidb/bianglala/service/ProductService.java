package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;
import com.gitlab.muhammadkholidb.bianglala.data.repository.ProductRepository;
import com.gitlab.muhammadkholidb.bianglala.utility.ConfigurationHolder;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilter;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductSearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Cacheable("searchProduct")
    public List<ProductSearchResult> searchProduct(ProductFilter param) {
        log.debug("Filter products");
        String languageId = ConfigurationHolder.getConfiguration(ConfigurationConstants.LANGUAGE_ID);
        return productRepository.filter(param, Long.valueOf(languageId));
    }

}
