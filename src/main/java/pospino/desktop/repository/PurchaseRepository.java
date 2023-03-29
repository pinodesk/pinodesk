package pospino.desktop.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pospino.desktop.domain.Purchase;
import pospino.desktop.viewmodel.MonthlyPurchaseTransactionVM;
import pospino.desktop.viewmodel.TotalPurchaseTransactionVM;

@Repository
public interface PurchaseRepository extends PagingAndSortingRepository<Purchase, Long>, PurchaseRepositoryCustom {

    boolean existsByInvoiceNumberIgnoreCaseAndSupplierIdAndDeletedAtIsNull(String invoiceNumber, Long supplierId);

    @Transactional
    @Modifying
    @Query("update purchase set updated_at=now(), deleted_at=now() where id in (:ids)")
    Long deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    Optional<Purchase> findByIdAndDeletedAtIsNull(Long purchaseId);

    @Query("""
            select
            count(id) as total_transaction,
            sum(total_payment) as total_payment
            from purchase
            where deleted_at is null and invoice_date between :start and :end
            """)
    TotalPurchaseTransactionVM findTotalPurchaseTransaction(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("""
            select avg(monthly)
            from (
                select sum(total_payment) as monthly
                from purchase
                where deleted_at is null and invoice_date between :start and :end
                group by month(invoice_date))
            """)
    BigDecimal findAverageMonthlyExpense(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
            select sum(total_payment) as monthly
            from purchase
            where deleted_at is null
            and invoice_date between :start and :end
            and month(invoice_date) = month(now())
            group by month(invoice_date)
                """)
    Optional<BigDecimal> findCurrentMonthExpense(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
            select
            month(p.invoice_date) as month_number,
            sum(p.total_payment) as total_payment,
            count(p.id) as total_transaction
            from purchase p
            where p.deleted_at is null and p.invoice_date between :start and :end
            group by month_number
            """)
    List<MonthlyPurchaseTransactionVM> findMonthlyPurchaseTransactions(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

}
