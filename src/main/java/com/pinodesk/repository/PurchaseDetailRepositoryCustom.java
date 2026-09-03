package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.PurchaseProductVM;
import com.pinodesk.viewmodel.PurchaseReportFilterVM;
import com.pinodesk.viewmodel.PurchaseReportVM;

public interface PurchaseDetailRepositoryCustom {

    List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String language);

    List<PurchaseReportVM> findByFilter(PurchaseReportFilterVM filter, String language);
}
