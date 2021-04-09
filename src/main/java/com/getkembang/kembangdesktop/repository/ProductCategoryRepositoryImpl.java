package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.ProductCategory;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Limit;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class ProductCategoryRepositoryImpl extends AbstractRepository<ProductCategory>
        implements ProductCategoryRepository {

    @Override
    public List<ProductCategory> filter(String keyword, Long languageId) {
        return read(new Where().equals(ProductCategory.C_LANGUAGE_ID, languageId)
                .andContainsIgnoreCase(ProductCategory.C_NAME, keyword), new Limit(10));
    }

}
