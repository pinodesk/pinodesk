package toska.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toska.desktop.domain.Purchase;
import toska.desktop.viewmodel.PurchaseFilterVM;

public interface PurchaseRepository extends CommonRepository<Purchase> {
    
	List<Purchase> filter(PurchaseFilterVM filter);
    
}
