package pinus.desktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Doctor;

@Repository
public interface DoctorRepository extends PagingAndSortingRepository<Doctor, Long>, DoctorRepositoryCustom {

    List<Doctor> findByDeletedAtIsNull();

    boolean existsByCodeIgnoreCaseAndDeletedAtIsNull(String code);

    boolean existsByRegistrationNumberAndDeletedAtIsNull(String code);

    boolean existsByMedicalLicenseNumberAndDeletedAtIsNull(String code);

    Optional<Doctor> findFirstByCodeStartingWithOrderByCodeDesc(String prefix);
}
