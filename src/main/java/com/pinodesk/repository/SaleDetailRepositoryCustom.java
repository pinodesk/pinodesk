package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.SaleProductVM;
import com.pinodesk.viewmodel.SaleReportFilterVM;
import com.pinodesk.viewmodel.SaleReportVM;

public interface SaleDetailRepositoryCustom {

    List<SaleProductVM> findBySaleIdJoinProducts(Long saleId, String language);

    List<SaleReportVM> findByFilter(SaleReportFilterVM filter, String language);
}
