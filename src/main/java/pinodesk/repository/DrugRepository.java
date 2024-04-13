package pinodesk.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.domain.Drug;

@Repository
public interface DrugRepository extends PagingAndSortingRepository<Drug, Long> {

    Optional<Drug> findByProductIdAndDeletedAtIsNull(Long productId);

    @Transactional
    @Modifying
    @Query("delete from drug where product_id = :productId")
    Long deleteByProductId(@Param("productId") Long productId);
}
