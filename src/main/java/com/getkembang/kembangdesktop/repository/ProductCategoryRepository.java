package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.ProductCategory;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface ProductCategoryRepository extends CommonRepository<ProductCategory> {
    
    List<ProductCategory> filter(String keyword, String languageCode);

}
