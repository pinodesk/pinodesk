package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.PurchaseDetail;
import pinus.desktop.viewmodel.PurchaseProductVM;

public interface PurchaseDetailRepository extends CommonRepository<PurchaseDetail> {

    List<PurchaseDetail> findByProductId(Long productId);

    List<PurchaseDetail> findByPurchaseId(Long purchaseId);

    List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String languageCode);

    void deleteByPurchaseId(Long purchaseId);
}
