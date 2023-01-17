package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.SaleFilterVM;
import pospino.desktop.viewmodel.SaleVM;

public interface SaleRepositoryCustom {

    List<SaleVM> findByFilter(SaleFilterVM filter);

}
