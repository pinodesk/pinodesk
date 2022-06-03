package pinus.desktop.repository;

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

    Optional<ProductExpiry> findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);

    Optional<ProductExpiry> findFirstByProductIdAndDeletedAtIsNullOrderByExpiredDate(Long productId);

    Optional<ProductExpiry> findByIdAndDeletedAtIsNull(Long productExpiryId);

    @Query("""
            select
                product_id,
                expired_date,
                (sum(coalesce(quantity_in,0))-sum(coalesce(quantity_out,0))) as quantity
            from product_expiry
            where product_id = :productId and deleted_at is null
            group by product_id, expired_date
            having quantity > 0;
            """)
    List<GroupedProductExpiryVM> findGroupedByProductId(@Param("productId") Long productId);

}
