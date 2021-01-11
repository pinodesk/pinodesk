package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.ProductCategory;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class ProductCategoryRepositoryImpl extends AbstractRepository<ProductCategory>
        implements ProductCategoryRepository {

    @Override
    public List<ProductCategory> filter(String keyword, Long languageId) {
        return read(new Where().equals(ProductCategory.C_LANGUAGE_ID, languageId)
                .andContainsIgnoreCase(ProductCategory.C_NAME, keyword), limitFactory.create(10));
    }

}
