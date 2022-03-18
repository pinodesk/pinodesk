package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.PurchaseDetail;

public interface PurchaseDetailRepository extends CommonRepository<PurchaseDetail> {

    List<PurchaseDetail> findByProductId(Long productId);
}
