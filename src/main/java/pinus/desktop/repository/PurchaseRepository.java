package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.Purchase;
import pinus.desktop.viewmodel.PurchaseFilterVM;
import pinus.desktop.viewmodel.PurchaseVM;

public interface PurchaseRepository extends CommonRepository<Purchase> {

    List<PurchaseVM> filter(PurchaseFilterVM filter);

    boolean existsByInvoiceNumberAndSupplierId(String orderNumber, Long supplierId);
}
