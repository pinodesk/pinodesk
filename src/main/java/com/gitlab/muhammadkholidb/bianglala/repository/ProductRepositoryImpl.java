package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.ArrayList;
import java.util.List;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.gitlab.muhammadkholidb.bianglala.domain.Product;
import com.gitlab.muhammadkholidb.bianglala.domain.ProductCategory;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.ProductEditVM;
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

    @Override
    public Integer updateProduct(ProductEditVM productEdit) {
        return update(new String[] {
            Product.C_NAME,
            Product.C_DESCRIPTION,
            Product.C_CODE,
            Product.C_BARCODE,
            Product.C_CATEGORY_CODE,
            Product.C_UNIT_ID,
            Product.C_UNIT_LABEL,
            Product.C_QUANTITY,
            Product.C_PURCHASE_PRICE,
            Product.C_SELLING_PRICE,
            Product.C_VAT_INCLUDED,
            Product.C_EXPIRED_DATE,
            Product.C_RACK_ID,
            Product.C_RACK_CODE
        }, new Object[] {
            productEdit.getName(),
            productEdit.getDescription(),
            productEdit.getCode(),
            productEdit.getBarcode(),
            productEdit.getProductCategory().getCode(),
            productEdit.getUnit().getId(),
            productEdit.getUnit().getLabel(),
            productEdit.getQuantity(),
            productEdit.getPurchasePrice(),
            productEdit.getSellingPrice(),
            productEdit.getVatIncluded(),
            productEdit.getExpiredDate(),
            productEdit.getRack() == null ? null : productEdit.getRack().getId(),
            productEdit.getRack() == null ? null : productEdit.getRack().getCode()
        }, productEdit.getId());
    }

}
