package pospino.desktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pospino.desktop.domain.Purchase;

@Repository
public interface PurchaseRepository extends PagingAndSortingRepository<Purchase, Long>, PurchaseRepositoryCustom {

    boolean existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull(String invoiceNumber, Long supplierId);

    @Transactional
    @Modifying
    @Query("update purchase set updated_at=now(), deleted_at=now() where id in (:ids)")
    Long deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    Optional<Purchase> findByIdAndDeletedAtIsNull(Long purchaseId);

}
