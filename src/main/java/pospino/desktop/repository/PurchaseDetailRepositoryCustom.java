package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.PurchaseProductVM;
import pospino.desktop.viewmodel.PurchaseReportFilterVM;
import pospino.desktop.viewmodel.PurchaseReportVM;

public interface PurchaseDetailRepositoryCustom {

    List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String language);

    List<PurchaseReportVM> findByFilter(PurchaseReportFilterVM filter, String language);
}
