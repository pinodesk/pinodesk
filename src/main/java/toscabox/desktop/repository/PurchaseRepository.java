package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.Purchase;
import toscabox.desktop.viewmodel.PurchaseFilterVM;

public interface PurchaseRepository extends CommonRepository<Purchase> {
    
	List<Purchase> filter(PurchaseFilterVM filter);
    
}
