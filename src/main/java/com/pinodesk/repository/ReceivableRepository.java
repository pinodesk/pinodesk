package com.pinodesk.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.pinodesk.entity.Receivable;
import com.pinodesk.viewmodel.ReceivableClosestDueDateVM;
import com.pinodesk.viewmodel.ReceivableVM;

@Repository
public interface ReceivableRepository extends PagingAndSortingRepository<Receivable, Long>, ReceivableRepositoryCustom {

    Optional<Receivable> findBySaleId(Long saleId);

    @Query("""
            select r.id as receivable_id, c.name as customer_name, r.invoice_number , r.invoice_date , r.due_date
            from receivable r
            join customer c on c.id = r.customer_id
            where r.completion_date is null
            and r.due_date < :dueDate
            and r.deleted_at is null
            order by r.due_date
            """)
    List<ReceivableClosestDueDateVM> findByDueDateBefore(@Param("dueDate") LocalDate dueDate);

    @Transactional
    @Modifying
    @Query("delete from receivable where sale_id in (:saleIds)")
    Long deleteBySaleIdIn(@Param("saleIds") List<Long> saleIds);

    @Query("""
            select a.*, b.id as customer_id, b.name as customer_name
            from receivable a
            inner join customer b on b.id = a.customer_id
            where a.id = :id and a.deleted_at is null
            """)
    Optional<ReceivableVM> findByIdJoinCustomer(@Param("id") Long id);

}
