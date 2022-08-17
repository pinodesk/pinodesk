package pinus.desktop.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.domain.PayablePayment;

@Repository
public interface PayablePaymentRepository extends PagingAndSortingRepository<PayablePayment, Long> {

    @Transactional
    @Modifying
    @Query("delete from payable_payment where payable_id = :payableId")
    Long deleteByPayableId(@Param("payableId") Long payableId);

    List<PayablePayment> findByPayableIdAndDeletedAtIsNull(Long payableId);

}
