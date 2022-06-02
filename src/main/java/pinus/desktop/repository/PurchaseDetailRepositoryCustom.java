package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.PurchaseProductVM;

public interface PurchaseDetailRepositoryCustom {

    List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String languageCode);

}
