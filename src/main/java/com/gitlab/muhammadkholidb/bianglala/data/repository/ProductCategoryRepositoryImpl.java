package com.gitlab.muhammadkholidb.bianglala.data.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.data.model.ProductCategory;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ProductCategoryRepositoryImpl extends AbstractRepository<ProductCategory> implements ProductCategoryRepository {

    @Override
    public List<ProductCategory> filter(String keyword, Long languageId) {
        return read(new Where()
                .equals(ProductCategory.C_LANGUAGE_ID, languageId)
                .andLikeIgnoreCase(ProductCategory.C_NAME, StringUtils.join("%", keyword, "%")), 
                limitFactory.create(10));
    }

}
