package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.SaleFilterVM;
import pinus.desktop.viewmodel.SaleVM;

public interface SaleRepositoryCustom {

    List<SaleVM> findByFilter(SaleFilterVM filter);

}
