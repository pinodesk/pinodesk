package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.PurchaseFilterVM;
import pospino.desktop.viewmodel.PurchaseVM;

public interface PurchaseRepositoryCustom {

    List<PurchaseVM> findByFilter(PurchaseFilterVM filter);

}
