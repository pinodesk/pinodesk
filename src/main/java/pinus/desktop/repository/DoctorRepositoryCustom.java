package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.DoctorVM;

public interface DoctorRepositoryCustom {

    List<DoctorVM> findByKeyword(String keyword, String language);

}
