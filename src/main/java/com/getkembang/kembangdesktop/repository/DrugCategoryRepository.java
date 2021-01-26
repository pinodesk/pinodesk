package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.DrugCategory;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.CommonRepository;

public interface DrugCategoryRepository extends CommonRepository<DrugCategory> {
    
    List<DrugCategory> filter(String keyword, Long drugCategoryBaseId);

}
