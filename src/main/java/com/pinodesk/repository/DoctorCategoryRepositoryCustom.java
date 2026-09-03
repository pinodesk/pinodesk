package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.entity.DoctorCategory;

public interface DoctorCategoryRepositoryCustom {

    List<DoctorCategory> findByKeyword(String keyword, String language);

}
