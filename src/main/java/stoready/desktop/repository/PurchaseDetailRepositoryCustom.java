package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.PurchaseProductVM;

public interface PurchaseDetailRepositoryCustom {

    List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String language);

}
