package pinodesk.repository;

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

import pinodesk.domain.Sale;
import pinodesk.viewmodel.BestSellingProductCategoryVM;
import pinodesk.viewmodel.BestSellingProductVM;
import pinodesk.viewmodel.LowestSellingProductVM;
import pinodesk.viewmodel.MonthlySaleTransactionVM;
import pinodesk.viewmodel.TotalSaleTransactionVM;

@Repository
public interface SaleRepository extends PagingAndSortingRepository<Sale, Long>, SaleRepositoryCustom {

    @Transactional
    @Modifying
    @Query("update sale set updated_at=now(), deleted_at=now() where id in (:ids)")
    Long deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    boolean existsByInvoiceNumberIgnoreCaseAndDeletedAtIsNull(String invoiceNumber);

    Optional<Sale> findByIdAndDeletedAtIsNull(Long saleId);

    @Query("""
            select
            count(id) as total_transaction,
            sum(total_payment) as total_payment
            from sale
            where deleted_at is null and created_at between :start and :end
            """)
    TotalSaleTransactionVM findTotalSaleTransaction(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
            select avg(monthly)
            from (
                select sum(total_payment) as monthly
                from sale
                where deleted_at is null and created_at between :start and :end
                group by month(created_at))
            """)
    BigDecimal findAverageMonthlyRevenue(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
            select sum(total_payment)
            from sale
            where deleted_at is null
            and created_at between :start and :end
            and month(created_at) = month(now())
            group by month(created_at)
            """)
    Optional<BigDecimal> findCurrentMonthRevenue(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("""
            select
            pc.name as category_name,
            sum(sd.sum_qty) as sum_category
            from product p
            join (
                select sd.product_id, sum(sd.quantity) as sum_qty
                from sale_detail sd
                inner join sale s on s.id = sd.sale_id and s.deleted_at is null
                where s.created_at between :start and :end
                group by sd.product_id
                order by sum_qty desc) as sd on sd.product_id = p.id and p.deleted_at is null
            join product_category pc on pc.code = p.category_code and pc.language = :language
            group by pc.name
            order by sum_category desc
            limit 5
            """)
    List<BestSellingProductCategoryVM> findBestSellingProductCategories(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("language") String language);

    @Query("""
            select
            month(s.created_at) as month_number,
            sum(s.total_payment) as total_payment,
            count(s.id) as total_transaction
            from sale s
            where s.deleted_at is null and s.created_at between :start and :end
            group by month_number
            """)
    List<MonthlySaleTransactionVM> findMonthlySaleTransactions(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("""
            select
            p.name as product_name,
            u.label as unit_label,
            pc.name as category_name,
            coalesce (sd.sum_qty,0) as sold_quantity
            from product p
            inner join (
                    select sd.product_id, sum(sd.quantity) as sum_qty
                    from sale_detail sd
                    inner join sale s on s.id = sd.sale_id and s.deleted_at is null
                    where s.created_at between :start and :end
                    group by sd.product_id) as sd on sd.product_id = p.id
            join product_category pc on pc.code = p.category_code and pc.language = :language
            join unit u on u.code = p.unit_code and u.language = :language
            where p.deleted_at is null and sd.sum_qty > 10
            order by sold_quantity desc
            limit 100
                """)
    List<BestSellingProductVM> findBestSellingProducts(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("language") String language);

    @Query("""
            select
            p.name as product_name,
            u.label as unit_label,
            pc.name as category_name,
            coalesce (sd.sum_qty,0) as sold_quantity
            from product p
            left join (
                    select sd.product_id, sum(sd.quantity) as sum_qty
                    from sale_detail sd
                    inner join sale s on s.id = sd.sale_id and s.deleted_at is null
                    where s.created_at between :start and :end
                    group by sd.product_id) as sd on sd.product_id = p.id
            join product_category pc on pc.code = p.category_code and pc.language = :language
            join unit u on u.code = p.unit_code and u.language = :language
            where p.deleted_at is null and coalesce (sd.sum_qty,0) < 10
            order by sold_quantity
                """)
    List<LowestSellingProductVM> findLowestSellingProducts(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("language") String language);

}
