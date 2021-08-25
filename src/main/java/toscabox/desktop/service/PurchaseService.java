package toscabox.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import toscabox.desktop.constant.CacheNameConstants;
import toscabox.desktop.repository.PurchaseRepository;
import toscabox.desktop.viewmodel.PurchaseFilterVM;
import toscabox.desktop.viewmodel.PurchaseVM;

@Service
public class PurchaseService extends BaseService {

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Cacheable(CacheNameConstants.PURCHASES_BY_FILTER)
    public List<PurchaseVM> searchPurchases(PurchaseFilterVM filter) {
        return objectConverter.convertList(purchaseRepository.filter(filter), PurchaseVM.class);
    }

}
