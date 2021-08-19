package toska.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import toska.desktop.domain.Purchase;
import toska.desktop.viewmodel.PurchaseFilterVM;

@Repository
public class PurchaseRepositoryImpl extends AbstractRepository<Purchase> implements PurchaseRepository {

    @Override
    public List<Purchase> filter(PurchaseFilterVM filter) {
        return read();
    }
    
}
