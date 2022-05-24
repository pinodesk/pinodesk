package pinus.desktop.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Doctor;

@Repository
public interface DoctorRepository extends PagingAndSortingRepository<Doctor, Long>, DoctorRepositoryCustom {

    List<Doctor> findByDeletedAtIsNull();

}
