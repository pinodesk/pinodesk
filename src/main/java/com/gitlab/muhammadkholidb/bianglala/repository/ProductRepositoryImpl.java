package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.ArrayList;
import java.util.List;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.gitlab.muhammadkholidb.bianglala.domain.Product;
import com.gitlab.muhammadkholidb.bianglala.domain.ProductCategory;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductFilterVM;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class ProductRepositoryImpl extends AbstractRepository<Product> implements ProductRepository {

    @Override
    public List<ProductVM> filter(ProductFilterVM filter, Long languageId) {
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
        log.debug("Formatted SQL: \n{}", SqlFormatter.format(sb.toString()));
        return jdbcTemplate.query(sb.toString(), params.toArray(), BeanPropertyRowMapper.newInstance(ProductVM.class));
    }

}
