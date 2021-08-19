package toska.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import toska.desktop.constant.CacheName;
import toska.desktop.repository.PurchaseRepository;
import toska.desktop.viewmodel.PurchaseFilterVM;
import toska.desktop.viewmodel.PurchaseVM;

@Service
public class PurchaseService extends BaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Cacheable(CacheName.Keys.PURCHASES_BY_FILTER)
    public List<PurchaseVM> searchPurchases(PurchaseFilterVM filter) {
        return objectConverter.convertList(purchaseRepository.filter(filter), PurchaseVM.class);
    }

}
