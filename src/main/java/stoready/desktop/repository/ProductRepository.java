package stoready.desktop.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import stoready.desktop.domain.Product;

@Repository
public interface ProductRepository extends PagingAndSortingRepository<Product, Long>, ProductRepositoryCustom {

    boolean existsByCodeAndDeletedAtIsNull(String code);

    boolean existsByBarcodeAndDeletedAtIsNull(String barcode);

    boolean existsByNameIgnoreCaseAndUnitIdAndDeletedAtIsNull(String name, Long unitId);

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

}
