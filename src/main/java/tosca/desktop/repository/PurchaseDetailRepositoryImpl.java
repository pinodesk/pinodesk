package tosca.desktop.repository;

import tosca.desktop.domain.PurchaseDetail;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

@Repository
public class PurchaseDetailRepositoryImpl extends AbstractRepository<PurchaseDetail> implements PurchaseDetailRepository {
    
}
