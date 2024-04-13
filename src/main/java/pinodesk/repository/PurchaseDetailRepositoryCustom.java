package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.PurchaseProductVM;
import pinodesk.viewmodel.PurchaseReportFilterVM;
import pinodesk.viewmodel.PurchaseReportVM;

public interface PurchaseDetailRepositoryCustom {

    List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String language);

    List<PurchaseReportVM> findByFilter(PurchaseReportFilterVM filter, String language);
}
