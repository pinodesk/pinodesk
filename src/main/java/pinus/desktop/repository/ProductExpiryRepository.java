package pinus.desktop.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.ProductExpiry;
import pinus.desktop.viewmodel.GroupedProductExpiryVM;

@Repository
public interface ProductExpiryRepository extends PagingAndSortingRepository<ProductExpiry, Long> {

    List<ProductExpiry> findByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);

    Optional<ProductExpiry> findFirstByProductIdOrderByIdDesc(Long productId);

    Optional<ProductExpiry> findFirstByProductIdAndExpiredDateOrderByIdDesc(Long productId, LocalDate expiredDate);

    Optional<ProductExpiry> findByIdAndDeletedAtIsNull(Long productExpiryId);

    @Query("""
            select
                product_id,
                expired_date,
                array_agg(final_quantity_expired_date order by id desc)[1] as quantity
            from product_expiry
            where product_id = :productId
            group by product_id, expired_date
            having quantity > 0
            order by expired_date
            """)
    List<GroupedProductExpiryVM> findGroupedByProductId(@Param("productId") Long productId);

    @Query("""
            select a.expired_date
            from product_expiry a
            where id in (
                select max(id)
                from product_expiry b
                where b.product_id = :productId
                group by b.product_id, b.expired_date
                order by b.expired_date)
            and a.final_quantity_expired_date > 0
            limit 1
            """)
    Optional<LocalDate> findClosestExpiredDateAvailableByProductId(@Param("productId") Long productId);

}
