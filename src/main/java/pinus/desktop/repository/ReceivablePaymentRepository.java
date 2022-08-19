package pinus.desktop.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.domain.ReceivablePayment;

@Repository
public interface ReceivablePaymentRepository extends PagingAndSortingRepository<ReceivablePayment, Long> {

    @Transactional
    @Modifying
    @Query("delete from receivable_payment where receivable_id = :receivableId")
    Long deleteByReceivableId(@Param("receivableId") Long receivableId);

    List<ReceivablePayment> findByReceivableIdAndDeletedAtIsNull(Long receivableId);

    @Query("""
            select count(a.id) > 0
            from receivable_payment a
            inner join receivable b on b.id = a.receivable_id
            where b.sale_id = :saleId and b.deleted_at is null
            """)
    boolean existsBySaleId(@Param("saleId") Long saleId);
}
