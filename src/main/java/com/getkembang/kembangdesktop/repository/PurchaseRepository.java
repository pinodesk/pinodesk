package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Purchase;
import com.getkembang.kembangdesktop.viewmodel.PurchaseFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface PurchaseRepository extends CommonRepository<Purchase> {
    
	List<Purchase> filter(PurchaseFilterVM filter);
    
}
