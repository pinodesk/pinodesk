package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Product;
import com.getkembang.kembangdesktop.viewmodel.ProductAddVM;
import com.getkembang.kembangdesktop.viewmodel.ProductEditVM;
import com.getkembang.kembangdesktop.viewmodel.ProductFilterVM;
import com.getkembang.kembangdesktop.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface ProductRepository extends CommonRepository<Product> {
    
    List<ProductVM> filter(ProductFilterVM filter, Long languageId);

    Integer updateProduct(ProductEditVM productEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByBarcode(String barcode, Long... excludedIds);

    boolean existsByNameAndUnit(String name, Long unitId, Long... excludedIds);

    Long createProduct(ProductAddVM productAdd);

}
