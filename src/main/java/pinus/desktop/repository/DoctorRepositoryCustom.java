package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.Doctor;

public interface DoctorRepositoryCustom {

    List<Doctor> findByKeyword(String keyword);

}
