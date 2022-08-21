package stoready.desktop.repository;

import static com.gitlab.muhammadkholidb.sequel.utility.SQLUtils.likeValueContains;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.utility.SQLUtils;
import com.gitlab.muhammadkholidb.sequel.utility.WhereParamsHelper;
import com.gitlab.muhammadkholidb.toolbox.data.ListBuilder;

import stoready.desktop.constant.ProductStatus;
import stoready.desktop.domain.Product;
import stoready.desktop.viewmodel.ProductCategoryVM;
import stoready.desktop.viewmodel.ProductFilterVM;
import stoready.desktop.viewmodel.ProductVM;
import stoready.desktop.viewmodel.UnitVM;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

public class ProductRepositoryImpl extends AbstractRepository<Product> implements ProductRepositoryCustom {

    @Override
    public List<ProductVM> findByFilter(ProductFilterVM filter, String language) {
        String sql = """
                select
                    a.*,
                    b.id as category_id,
                    b.name as category_name
                from product a
                inner join product_category b on b.code = a.category_code and b.language = ?
                where a.deleted_at is null
                """;
        WhereParamsHelper whereParamsHelper = where(filter);
        sql = sql + whereParamsHelper.getQueryAppend().toString();
        whereParamsHelper.getParams().add(0, language);
        return performSelect(sql, whereParamsHelper.getParams(), ProductVM.class);
    }

    private WhereParamsHelper where(ProductFilterVM filter) {
        String name = filter.getName();
        String description = filter.getDescription();
        String code = filter.getCode();
        String barcode = filter.getBarcode();
        ProductCategoryVM category = filter.getCategory();
        UnitVM unit = filter.getUnit();
        ProductStatus status = filter.getStatus();
        Integer quantityMax = filter.getStockQuantityMax();
        Integer quantityMin = filter.getStockQuantityMin();
        BigDecimal generalSellingPriceMax = filter.getGeneralSellingPriceMax();
        BigDecimal generalSellingPriceMin = filter.getGeneralSellingPriceMin();
        BigDecimal prescriptionSellingPriceMax = filter.getPrescriptionSellingPriceMax();
        BigDecimal prescriptionSellingPriceMin = filter.getPrescriptionSellingPriceMin();
        WhereParamsHelper helper = new WhereParamsHelper();
        if (StringUtils.isNotBlank(name)) {
            helper.getQueryAppend().append(" AND LOWER(a.name) LIKE ? ");
            helper.getParams().add(likeValueContains(name.toLowerCase()));
        }
        if (StringUtils.isNotBlank(description)) {
            helper.getQueryAppend().append(" AND LOWER(a.description) LIKE ? ");
            helper.getParams().add(likeValueContains(description.toLowerCase()));
        }
        if (StringUtils.isNotBlank(code)) {
            helper.getQueryAppend().append(" AND LOWER(a.code) LIKE ? ");
            helper.getParams().add(likeValueContains(code.toLowerCase()));
        }
        if (StringUtils.isNotBlank(barcode)) {
            helper.getQueryAppend().append(" AND LOWER(a.barcode) LIKE ? ");
            helper.getParams().add(likeValueContains(barcode.toLowerCase()));
        }
        if (category != null) {
            helper.getQueryAppend().append(" AND a.category_code = ? ");
            helper.getParams().add(category.getCode());
        }
        if (unit != null) {
            helper.getQueryAppend().append(" AND a.unit_id = ? ");
            helper.getParams().add(unit.getId());
        }
        if (quantityMin != null) {
            helper.getQueryAppend().append(" AND a.quantity >= ? ");
            helper.getParams().add(quantityMin);
        }
        if (quantityMax != null) {
            helper.getQueryAppend().append(" AND a.quantity <= ? ");
            helper.getParams().add(quantityMax);
        }
        if (generalSellingPriceMin != null) {
            helper.getQueryAppend().append(" AND a.general_selling_price >= ? ");
            helper.getParams().add(generalSellingPriceMin);
        }
        if (generalSellingPriceMax != null) {
            helper.getQueryAppend().append(" AND a.general_selling_price <= ? ");
            helper.getParams().add(generalSellingPriceMax);
        }
        if (prescriptionSellingPriceMin != null) {
            helper.getQueryAppend().append(" AND a.prescription_selling_price >= ? ");
            helper.getParams().add(prescriptionSellingPriceMin);
        }
        if (prescriptionSellingPriceMax != null) {
            helper.getQueryAppend().append(" AND a.prescription_selling_price <= ? ");
            helper.getParams().add(prescriptionSellingPriceMax);
        }
        if (status != null) {
            helper.getQueryAppend().append(" AND a.status = ? ");
            helper.getParams().add(status.toString());
        }
        appendQueryForProductExpiry(filter, helper);
        return helper;
    }

    private void appendQueryForProductExpiry(ProductFilterVM filter, WhereParamsHelper helper) {
        LocalDate expiredDateMax = filter.getExpiredDateMax();
        LocalDate expiredDateMin = filter.getExpiredDateMin();
        String batchNumber = filter.getBatchNumber();
        if (ObjectUtils.anyNotNull(expiredDateMax, expiredDateMin) || StringUtils.isNotBlank(batchNumber)) {
            helper.getQueryAppend().append(" and (select count(c.id) from product_expiry c where c.product_id = a.id ");
            if (expiredDateMax != null) {
                helper.getQueryAppend().append(" and c.expired_date <= ? ");
                helper.getParams().add(expiredDateMax);
            }
            if (expiredDateMin != null) {
                helper.getQueryAppend().append(" and c.expired_date >= ? ");
                helper.getParams().add(expiredDateMin);
            }
            if (StringUtils.isNotBlank(batchNumber)) {
                helper.getQueryAppend().append(" and c.batch_number like ? ");
                helper.getParams().add(SQLUtils.likeValueContains(batchNumber));
            }
            helper.getQueryAppend().append(" ) > 0 ");
        }
    }

    @Override
    public List<ProductVM> findByKeyword(String keyword, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select
                    a.*,
                    b.id as category_id,
                    b.name as category_name
                from product a
                inner join product_category b on b.code = a.category_code and b.language = ?
                where a.deleted_at is null
                """);
        ListBuilder<Object> lb = new ListBuilder<>().add(language);
        if (StringUtils.isNotBlank(keyword)) {
            String likeValue = SQLUtils.likeValueContains(keyword.trim().toLowerCase());
            sb.append("""
                    and (lower(a.name) like ?
                    or a.code like ?
                    or a.barcode like ?
                    or b.code like ?
                    or lower(b.name) like ?)
                    """);
            lb.add(likeValue).add(likeValue).add(likeValue).add(likeValue).add(likeValue);
        }
        return performSelect(sb.toString(), lb.build(), ProductVM.class);
    }

}
