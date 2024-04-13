package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.PurchaseFilterVM;
import pinodesk.viewmodel.PurchaseVM;

public interface PurchaseRepositoryCustom {

    List<PurchaseVM> findByFilter(PurchaseFilterVM filter);

}
