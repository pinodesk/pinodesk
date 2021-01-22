package com.gitlab.muhammadkholidb.bianglala.repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
        String name = filter.getName();
        String code = filter.getCode();
        String barcode = filter.getBarcode();
        String categoryCode = filter.getCategoryCode();
        Long unitId = filter.getUnitId();
        Integer quantityMax = filter.getQuantityMax();
        Integer quantityMin = filter.getQuantityMin();
        BigDecimal purchasePriceMax = filter.getPurchasePriceMax();
        BigDecimal purchasePriceMin = filter.getPurchasePriceMin();
        BigDecimal sellingPriceMax = filter.getSellingPriceMax();
        BigDecimal sellingPriceMin = filter.getSellingPriceMin();
        Date expiredDateMax = filter.getExpiredDateMax();
        Date expiredDateMin = filter.getExpiredDateMin();
        Long rackId = filter.getRackId();
        String includesVat = filter.getIncludesVat();
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT p.*, pc.id as category_id, pc.code as category_code, pc.name as category_name ");
        sb.append(" FROM ").append(Product.TABLE_NAME).append(" p ");
        sb.append(" LEFT JOIN ").append(ProductCategory.TABLE_NAME).append(" pc ")
                .append(" ON pc.code = p.category_code AND pc.deleted_at IS NULL AND pc.language_id = ? ");
        sb.append(" WHERE p.deleted_at IS NULL ");
        List<Object> params = new ArrayList<>();
        params.add(languageId);
        if (StringUtils.isNotBlank(name)) {
            sb.append(" AND LOWER(p.name) LIKE ? ");
            params.add(StringUtils.join("%", name.toLowerCase(), "%"));
        }
        if (StringUtils.isNotBlank(code)) {
            sb.append(" AND LOWER(p.code) LIKE ? ");
            params.add(StringUtils.join("%", code.toLowerCase(), "%"));
        }
        if (StringUtils.isNotBlank(barcode)) {
            sb.append(" AND LOWER(p.barcode) LIKE ? ");
            params.add(StringUtils.join("%", barcode.toLowerCase(), "%"));
        }
        if (StringUtils.isNotBlank(categoryCode)) {
            sb.append(" AND pc.code = ? ");
            params.add(categoryCode);
        }
        if (unitId != null) {
            sb.append(" AND p.unit_id = ? ");
            params.add(unitId);
        }
        if (quantityMin != null) {
            sb.append(" AND p.quantity >= ? ");
            params.add(quantityMin);
        }
        if (quantityMax != null) {
            sb.append(" AND p.quantity <= ? ");
            params.add(quantityMin);
        }
        if (purchasePriceMin != null) {
            sb.append(" AND p.purchase_price >= ? ");
            params.add(purchasePriceMin);
        }
        if (purchasePriceMax != null) {
            sb.append(" AND p.purchase_price <= ? ");
            params.add(purchasePriceMax);
        }
        if (sellingPriceMin != null) {
            sb.append(" AND p.selling_price >= ? ");
            params.add(sellingPriceMin);
        }
        if (sellingPriceMax != null) {
            sb.append(" AND p.selling_price <= ? ");
            params.add(sellingPriceMax);
        }
        if (expiredDateMin != null) {
            sb.append(" AND p.expired_date >= ? ");
            params.add(expiredDateMin);
        }
        if (expiredDateMax != null) {
            sb.append(" AND p.expired_date <= ? ");
            params.add(expiredDateMax);
        }
        if (rackId != null) {
            sb.append(" AND p.rack_id = ? ");
            params.add(rackId);
        }
        if (StringUtils.isNotBlank(includesVat)) {
            sb.append(" AND p.vat_included = ? ");
            params.add(includesVat);
        }
        log.debug("Formatted SQL: \n{}", SqlFormatter.format(sb.toString()));
        return jdbcTemplate.query(sb.toString(), params.toArray(), BeanPropertyRowMapper.newInstance(ProductVM.class));
    }

    // @formatter:off
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
    // @formatter:on

}
