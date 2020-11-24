package com.gitlab.muhammadkholidb.bianglala.data.repository;

import java.util.ArrayList;
import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.data.model.Product;
import com.gitlab.muhammadkholidb.bianglala.data.model.ProductCategory;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilter;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductSearchResult;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class ProductRepositoryImpl extends AbstractRepository<Product> implements ProductRepository {

    @Override
    public List<ProductSearchResult> filter(ProductFilter filter, Long languageId) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p.*, pc.id as category_id, pc.code as category_code, pc.name as category_name ");
        sb.append(" FROM ")
                .append(Product.TABLE_NAME)
                .append(" p ");
        sb.append(" LEFT JOIN ")
                .append(ProductCategory.TABLE_NAME)
                .append(" pc ON pc.code = p.category_code AND pc.deleted_at IS NULL AND pc.language_id = ? ");
        sb.append(" WHERE p.deleted_at IS NULL ");
        List<Object> params = new ArrayList<>();
        params.add(languageId);
        if (StringUtils.isNotBlank(filter.getName())) {
            sb.append(" AND LOWER(p.name) LIKE ? ");
            params.add(StringUtils.join("%", filter.getName().toLowerCase(), "%"));
        }
        if (StringUtils.isNotBlank(filter.getCode())) {
            sb.append(" AND LOWER(p.code) LIKE ? ");
            params.add(StringUtils.join("%", filter.getCode().toLowerCase(), "%"));
        }
        if (StringUtils.isNotBlank(filter.getCategoryCode())) {
            sb.append(" AND pc.code = ? ");
            params.add(filter.getCategoryCode());
        }
        return jdbcTemplate.query(sb.toString(), params.toArray(), BeanPropertyRowMapper.newInstance(ProductSearchResult.class));

    }

}
