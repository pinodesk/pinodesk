package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.entity.Supplier;
import com.pinodesk.viewmodel.SupplierFilterVM;

public interface SupplierRepositoryCustom {

    List<Supplier> findByFilter(SupplierFilterVM filter);

    List<Supplier> findByKeyword(String keyword);

}
