package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.SaleProductVM;
import pospino.desktop.viewmodel.SaleReportFilterVM;
import pospino.desktop.viewmodel.SaleReportVM;

public interface SaleDetailRepositoryCustom {

    List<SaleProductVM> findBySaleIdJoinProducts(Long saleId, String language);

    List<SaleReportVM> findByFilter(SaleReportFilterVM filter, String language);
}
