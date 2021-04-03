package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Supplier;
import com.getkembang.kembangdesktop.viewmodel.SupplierAddVM;
import com.getkembang.kembangdesktop.viewmodel.SupplierEditVM;
import com.getkembang.kembangdesktop.viewmodel.SupplierFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface SupplierRepository extends CommonRepository<Supplier> {
    
	List<Supplier> filter(SupplierFilterVM filter);
    
    Long createSupplier(SupplierAddVM supplierAdd);

    Integer updateSupplier(SupplierEditVM supplierEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByEmail(String email, Long... excludeIds);

    boolean existsByPhone(String phone, Long... excludeIds);

    String findMaxCodeByPrefix(String prefix);
}
