package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.SaleProductVM;
import pinodesk.viewmodel.SaleReportFilterVM;
import pinodesk.viewmodel.SaleReportVM;

public interface SaleDetailRepositoryCustom {

    List<SaleProductVM> findBySaleIdJoinProducts(Long saleId, String language);

    List<SaleReportVM> findByFilter(SaleReportFilterVM filter, String language);
}
