package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.SaleFilterVM;
import stoready.desktop.viewmodel.SaleVM;

public interface SaleRepositoryCustom {

    List<SaleVM> findByFilter(SaleFilterVM filter);

}
