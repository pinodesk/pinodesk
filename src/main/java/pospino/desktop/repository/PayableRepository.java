package pospino.desktop.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pospino.desktop.domain.Payable;
import pospino.desktop.viewmodel.PayableClosestDueDateVM;

@Repository
public interface PayableRepository extends PagingAndSortingRepository<Payable, Long>, PayableRepositoryCustom {

    Optional<Payable> findByPurchaseId(Long purchaseId);

    @Query("""
            select s.name as supplier_name, p.invoice_number, p.invoice_date, p.due_date
            from payable p
            join supplier s on s.id = p.supplier_id
            where completion_date is null
            and due_date < :dueDate
            order by due_date
            """)
    List<PayableClosestDueDateVM> findByDueDateBefore(@Param("dueDate") LocalDate dueDate);

}
