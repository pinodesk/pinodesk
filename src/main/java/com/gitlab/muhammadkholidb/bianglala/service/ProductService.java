package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;
import com.gitlab.muhammadkholidb.bianglala.repository.ProductRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilterVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ConfigurationService configurationService;

    @Cacheable("productsByFilter")
    public List<ProductVM> searchProduct(ProductFilterVM param) {
        String languageId = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE_ID);
        return productRepository.filter(param, Long.valueOf(languageId));
    }

}
