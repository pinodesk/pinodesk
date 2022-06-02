package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.PurchaseFilterVM;
import pinus.desktop.viewmodel.PurchaseVM;

public interface PurchaseRepositoryCustom {

    List<PurchaseVM> findByFilter(PurchaseFilterVM filter);

}
