package com.gitlab.muhammadkholidb.bianglala.repository;

import com.gitlab.muhammadkholidb.bianglala.entity.LanguageEntity;
import com.gitlab.muhammadkholidb.bianglala.entity.ProductEntity;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilter;
import java.util.List;

public interface ProductRepositoryCustom {
    
    List<ProductEntity> filter(ProductFilter filter, LanguageEntity language);
    
}
