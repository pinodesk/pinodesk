package pinus.desktop.repository;

import pinus.desktop.domain.DoctorCategory;

import java.util.List;

public interface DoctorCategoryRepositoryCustom {

    List<DoctorCategory> findByKeyword(String keyword, String languageCode);

}
