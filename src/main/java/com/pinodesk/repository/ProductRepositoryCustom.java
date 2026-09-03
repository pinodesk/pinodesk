package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.ProductFilterVM;
import com.pinodesk.viewmodel.ProductVM;

public interface ProductRepositoryCustom {

    List<ProductVM> findByFilter(ProductFilterVM filter, String language);

    List<ProductVM> findByKeyword(String keyword, String language);

}
