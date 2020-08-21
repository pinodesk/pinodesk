package com.gitlab.muhammadkholidb.bianglala.data.repository;

import com.gitlab.muhammadkholidb.bianglala.data.model.Product;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

@Repository
public class ProductRepositoryImpl extends AbstractRepository<Product> implements ProductRepository {
    
}
