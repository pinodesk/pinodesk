package com.gitlab.muhammadkholidb.bianglala.data.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.data.model.ProductCategory;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.CommonRepository;

public interface ProductCategoryRepository extends CommonRepository<ProductCategory> {
    
    List<ProductCategory> filter(String keyword, Long languageId);

}
