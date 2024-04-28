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

import pinodesk.entity.Receivable;
import pinodesk.viewmodel.ReceivableClosestDueDateVM;

@Repository
public interface ReceivableRepository extends PagingAndSortingRepository<Receivable, Long>, ReceivableRepositoryCustom {

    Optional<Receivable> findBySaleId(Long saleId);

    @Query("""
            select c.name as customer_name, r.invoice_number , r.invoice_date , r.due_date
            from receivable r
            join customer c on c.id = r.customer_id
            where r.completion_date is null
            and r.due_date < :dueDate
            order by r.due_date
            """)
    List<ReceivableClosestDueDateVM> findByDueDateBefore(@Param("dueDate") LocalDate dueDate);

    @Transactional
    @Modifying
    @Query("delete from receivable where sale_id in (:saleIds)")
    Long deleteBySaleIdIn(@Param("saleIds") List<Long> saleIds);

}
