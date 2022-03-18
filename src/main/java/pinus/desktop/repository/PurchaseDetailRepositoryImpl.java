package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.PurchaseDetail;

@Repository
public class PurchaseDetailRepositoryImpl extends AbstractRepository<PurchaseDetail>
        implements PurchaseDetailRepository {

    @Override
    public List<PurchaseDetail> findByProductId(Long productId) {
        return read(new Where().equals(PurchaseDetail.C_PRODUCT_ID, productId));
    }

}
