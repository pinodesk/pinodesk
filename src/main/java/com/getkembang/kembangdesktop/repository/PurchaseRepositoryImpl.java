package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Purchase;
import com.getkembang.kembangdesktop.viewmodel.PurchaseFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

@Repository
public class PurchaseRepositoryImpl extends AbstractRepository<Purchase> implements PurchaseRepository {

    @Override
    public List<Purchase> filter(PurchaseFilterVM filter) {
        return read();
    }
    
}
