package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.Purchase;
import tosca.desktop.viewmodel.PurchaseFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

@Repository
public class PurchaseRepositoryImpl extends AbstractRepository<Purchase> implements PurchaseRepository {

    @Override
    public List<Purchase> filter(PurchaseFilterVM filter) {
        return read();
    }
    
}
