package pinodesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.entity.Supplier;

@Repository
public interface SupplierRepository extends PagingAndSortingRepository<Supplier, Long>, SupplierRepositoryCustom {

    boolean existsByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);

    boolean existsByPhoneIgnoreCaseAndDeletedAtIsNull(String phone);

    Optional<Supplier> findFirstByCodeStartingWithOrderByCodeDesc(String prefix);

    List<Supplier> findByDeletedAtIsNull();

    @Transactional
    @Modifying
    @Query("update supplier set updated_at=now(), deleted_at=now() where id in (:ids)")
    Long deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    Optional<Supplier> findByIdAndDeletedAtIsNull(Long id);

}
