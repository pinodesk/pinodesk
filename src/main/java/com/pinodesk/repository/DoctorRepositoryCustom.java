package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.DoctorFilterVM;
import com.pinodesk.viewmodel.DoctorVM;

public interface DoctorRepositoryCustom {

    List<DoctorVM> findByKeyword(String keyword, String language);

    List<DoctorVM> findByFilter(DoctorFilterVM filter, String language);
}
