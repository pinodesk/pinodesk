package tosca.desktop.service;

import java.util.List;

import tosca.desktop.constant.CacheName;
import tosca.desktop.repository.PurchaseRepository;
import tosca.desktop.viewmodel.PurchaseFilterVM;
import tosca.desktop.viewmodel.PurchaseVM;

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
