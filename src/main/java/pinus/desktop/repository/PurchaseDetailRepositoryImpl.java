package pinus.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.PurchaseDetail;

@Repository
public class PurchaseDetailRepositoryImpl extends AbstractRepository<PurchaseDetail>
        implements PurchaseDetailRepository {

}
