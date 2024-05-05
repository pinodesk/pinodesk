package pinodesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.entity.Doctor;

@Repository
public interface DoctorRepository extends PagingAndSortingRepository<Doctor, Long>, DoctorRepositoryCustom {

    List<Doctor> findByDeletedAtIsNull();

    boolean existsByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    boolean existsByRegistrationNumberAndDeletedAtIsNull(String code);

    boolean existsByMedicalLicenseNumberAndDeletedAtIsNull(String code);

    boolean existsByEmailIgnoreCaseAndDeletedAtIsNull(String email);

    boolean existsByPhoneAndDeletedAtIsNull(String phone);

    Optional<Doctor> findFirstByCodeStartingWithOrderByCodeDesc(String prefix);

    @Transactional
    @Modifying
    @Query("update doctor set updated_at=now(), deleted_at=now() where id in (:ids)")
    Long deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    Optional<Doctor> findByIdAndDeletedAtIsNull(Long doctorId);

}
