package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.data.model.Product;
import com.gitlab.muhammadkholidb.bianglala.data.repository.ProductRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.read();
    }

    public List<Product> getProductsByKeyword(String keyword) {
        return productRepository.read(new Where().likeIgnoreCase(Product.C_NAME, "%" + keyword + "%").orLikeIgnoreCase(Product.C_CODE, "%" + keyword + "%"));
    }

}
