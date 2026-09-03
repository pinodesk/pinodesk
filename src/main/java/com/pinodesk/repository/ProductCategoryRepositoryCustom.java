package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.entity.ProductCategory;

public interface ProductCategoryRepositoryCustom {

    List<ProductCategory> findByKeyword(String keyword, String language);

}
