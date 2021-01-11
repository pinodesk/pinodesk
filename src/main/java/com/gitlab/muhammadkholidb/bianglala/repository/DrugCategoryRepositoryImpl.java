package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.DrugCategory;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class DrugCategoryRepositoryImpl extends AbstractRepository<DrugCategory> implements DrugCategoryRepository {

    @Override
    public List<DrugCategory> filter(String keyword, Long drugCategoryBaseId) {
        return read(new Where().equals(DrugCategory.C_CATEGORY_BASE_ID, drugCategoryBaseId)
                .andContainsIgnoreCase(DrugCategory.C_NAME, keyword), limitFactory.create(10));
    }

}
