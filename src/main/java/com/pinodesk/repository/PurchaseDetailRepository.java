package com.pinodesk.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.entity.PurchaseDetail;
import com.pinodesk.viewmodel.ProductPurchaseVM;

@Repository
public interface PurchaseDetailRepository
        extends PagingAndSortingRepository<PurchaseDetail, Long>, PurchaseDetailRepositoryCustom {

    List<PurchaseDetail> findByProductIdAndDeletedAtIsNull(Long productId);

    List<PurchaseDetail> findByPurchaseId(Long purchaseId);

    @Transactional
    @Modifying
    @Query("delete from purchase_detail where purchase_id = :purchaseId")
    Long deleteByPurchaseId(@Param("purchaseId") Long purchaseId);

    @Transactional
    @Modifying
    @Query("update purchase_detail set updated_at=now(), deleted_at=now() where purchase_id in (:purchaseIds)")
    Long deleteUpdateByPurchaseIdIn(@Param("purchaseIds") List<Long> purchaseIds);

    @Transactional
    @Modifying
    @Query("delete from purchase_detail where purchase_id in (:purchaseIds)")
    Long deleteByPurchaseIdIn(@Param("purchaseIds") List<Long> purchaseIds);

    @Query("""
            select
                a.*,
                b.invoice_number,
                b.invoice_date,
                b.payment_due_date,
                b.payment_status,
                c.id as supplier_id,
                c.name as supplier_name
            from purchase_detail a
            join purchase b on b.id = a.purchase_id
            join supplier c on c.id = b.supplier_id
            where a.product_id = :productId
            order by b.id desc
            """)
    List<ProductPurchaseVM> findByProductIdOrderByIdDesc(@Param("productId") Long productId);

}
