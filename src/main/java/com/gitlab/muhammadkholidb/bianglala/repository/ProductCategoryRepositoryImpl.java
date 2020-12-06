package com.gitlab.muhammadkholidb.bianglala.repository;

import static com.gitlab.muhammadkholidb.bianglala.constant.StringConstants.PERCENT;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.ProductCategory;
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
                .andLikeIgnoreCase(ProductCategory.C_NAME, StringUtils.join(PERCENT, keyword, PERCENT)), 
                limitFactory.create(10));
    }

}
