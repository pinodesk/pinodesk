package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.PurchaseFilterVM;
import stoready.desktop.viewmodel.PurchaseVM;

public interface PurchaseRepositoryCustom {

    List<PurchaseVM> findByFilter(PurchaseFilterVM filter);

}
