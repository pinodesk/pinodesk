package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.DrugCategory;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.CommonRepository;

public interface DrugCategoryRepository extends CommonRepository<DrugCategory> {
    
    List<DrugCategory> filter(String keyword, Long drugCategoryBaseId);

}
