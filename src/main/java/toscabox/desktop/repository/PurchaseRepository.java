package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.Purchase;
import toscabox.desktop.viewmodel.PurchaseFilterVM;
import toscabox.desktop.viewmodel.PurchaseVM;

public interface PurchaseRepository extends CommonRepository<Purchase> {
    
	List<PurchaseVM> filter(PurchaseFilterVM filter);
    
}
