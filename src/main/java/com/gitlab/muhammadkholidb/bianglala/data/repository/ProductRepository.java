package com.gitlab.muhammadkholidb.bianglala.data.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.data.model.Product;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilter;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductSearchResult;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.CommonRepository;

public interface ProductRepository extends CommonRepository<Product> {
    
    List<ProductSearchResult> filter(ProductFilter filter, Long languageId);

}
