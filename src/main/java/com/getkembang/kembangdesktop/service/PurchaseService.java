package com.getkembang.kembangdesktop.service;

import java.util.List;

import com.getkembang.kembangdesktop.constant.CacheName;
import com.getkembang.kembangdesktop.repository.PurchaseRepository;
import com.getkembang.kembangdesktop.viewmodel.PurchaseFilterVM;
import com.getkembang.kembangdesktop.viewmodel.PurchaseVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PurchaseService extends BaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Cacheable(CacheName.Keys.PURCHASES_BY_FILTER)
    public List<PurchaseVM> searchPurchases(PurchaseFilterVM filter) {
        return objectConverter.convertList(purchaseRepository.filter(filter), PurchaseVM.class);
    }

}
