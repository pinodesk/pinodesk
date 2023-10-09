package pospino.desktop.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pospino.desktop.domain.Product;
import pospino.desktop.viewmodel.ProductClosestExpiryVM;
import pospino.desktop.viewmodel.ProductOutOfStockVM;
import pospino.desktop.viewmodel.ProductVM;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, ProductRepositoryCustom {

    boolean existsByCodeAndDeletedAtIsNull(String code);

    boolean existsByBarcodeAndDeletedAtIsNull(String barcode);

    boolean existsByNameIgnoreCaseAndUnitCodeAndDeletedAtIsNull(String name, String unitCode);

    Optional<Product> findByIdAndDeletedAtIsNull(Long id);

    @Transactional
    @Modifying
    @Query("update product set updated_at=now(), deleted_at=now() where id in (:ids)")
    Integer deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    @Transactional
    @Modifying
    @Query("update product set updated_at=now(), closest_expired_date=:closestExpiredDate where id=:id")
    Integer updateClosestExpiredDateById(
            @Param("id") Long id,
            @Param("closestExpiredDate") LocalDate closestExpiredDate);

    @Query("""
            select
            a.*,
            b.id as category_id,
            b.name as category_name
            from product a
            inner join product_category b on b.code = a.category_code and b.language = :language
            where (a.code = :code or a.barcode = :code) and a.deleted_at is null
            """)
    Optional<ProductVM> findByCode(@Param("code") String code, @Param("language") String language);

    @Query("""
            select year(created_at) as year_created
            from product
            group by year_created
            order by year_created
            limit 1
            """)
    Optional<Integer> findMinCreatedYear();

    @Query("""
            select p.name as product_name, pc.name as category_name, p.closest_expired_date as expired_date
            from product p
            join product_category pc on pc.code = p.category_code and pc.language = :language
            where closest_expired_date < :expiredDate
            order by closest_expired_date
            """)
    List<ProductClosestExpiryVM> findByExpiredDateBefore(
            @Param("expiredDate") LocalDate expiredDate,
            @Param("language") String language);

    @Query("""
            select p.name as product_name, pc.name as category_name, p.quantity as quantity
            from product p
            join product_category pc on pc.code = p.category_code and pc.language = :language
            where quantity < :quantity
            order by quantity
            """)
    List<ProductOutOfStockVM> findByQuantityLowerThan(
            @Param("quantity") Integer quantity,
            @Param("language") String language);

}
