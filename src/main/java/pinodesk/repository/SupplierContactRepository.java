package pinodesk.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.entity.SupplierContact;

@Repository
public interface SupplierContactRepository extends PagingAndSortingRepository<SupplierContact, Long> {

    boolean existsByEmailIgnoreCaseAndSupplierIdAndDeletedAtIsNull(String email, Long supplierId);

    boolean existsByPhoneAndSupplierIdAndDeletedAtIsNull(String phone, Long supplierId);

    @Transactional
    @Modifying
    @Query("delete from supplier_contact where supplier_id = :supplierId")
    Long deleteBySupplierId(@Param("supplierId") Long supplierId);

    List<SupplierContact> findBySupplierIdAndDeletedAtIsNull(Long supplierId);

    @Transactional
    @Modifying
    @Query("update supplier_contact set updated_at=now(), deleted_at=now() where supplier_id in (:supplierIds)")
    Long deleteUpdateBySupplierIdIn(@Param("supplierIds") List<Long> supplierIds);

}
