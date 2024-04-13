package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.SaleFilterVM;
import pinodesk.viewmodel.SaleVM;

public interface SaleRepositoryCustom {

    List<SaleVM> findByFilter(SaleFilterVM filter);

}
