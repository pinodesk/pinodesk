package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.PurchaseProductVM;

public interface PurchaseDetailRepositoryCustom {

    List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String language);

}
