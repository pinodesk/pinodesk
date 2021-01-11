package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.Product;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductEditVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilterVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.CommonRepository;

public interface ProductRepository extends CommonRepository<Product> {
    
    List<ProductVM> filter(ProductFilterVM filter, Long languageId);

    Integer updateProduct(ProductEditVM productEdit);

}
