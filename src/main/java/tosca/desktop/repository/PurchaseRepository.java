package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.Purchase;
import tosca.desktop.viewmodel.PurchaseFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface PurchaseRepository extends CommonRepository<Purchase> {
    
	List<Purchase> filter(PurchaseFilterVM filter);
    
}
