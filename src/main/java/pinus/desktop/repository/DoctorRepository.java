package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.Doctor;

public interface DoctorRepository extends CommonRepository<Doctor> {

    List<Doctor> findByKeyword(String keyword);

}
