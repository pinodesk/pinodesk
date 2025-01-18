package pinodesk.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.entity.Payable;
import pinodesk.viewmodel.PayableClosestDueDateVM;
import pinodesk.viewmodel.PayableVM;

@Repository
public interface PayableRepository extends PagingAndSortingRepository<Payable, Long>, PayableRepositoryCustom {

    Optional<Payable> findByPurchaseId(Long purchaseId);

    @Query("""
            select p.id as payable_id, s.name as supplier_name, p.invoice_number, p.invoice_date, p.due_date
            from payable p
            join supplier s on s.id = p.supplier_id
            where p.completion_date is null
            and p.due_date < :dueDate
            and p.deleted_at is null
            order by p.due_date
            """)
    List<PayableClosestDueDateVM> findByDueDateBefore(@Param("dueDate") LocalDate dueDate);

    @Transactional
    @Modifying
    @Query("delete from payable where purchase_id in (:purchaseIds)")
    Long deleteByPurchaseIdIn(@Param("purchaseIds") List<Long> purchaseIds);

    @Query("""
            select a.*, b.id as supplier_id, b.name as supplier_name
            from payable a
            inner join supplier b on b.id = a.supplier_id
            where a.id = :id and a.deleted_at is null
            """)
    Optional<PayableVM> findByIdJoinSupplier(@Param("id") Long id);

}
