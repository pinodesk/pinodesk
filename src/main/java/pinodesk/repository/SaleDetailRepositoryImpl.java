package pinodesk.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.mudiatech.sequel.repository.AbstractRepository;
import com.mudiatech.sequel.sql.Where;

import pinodesk.entity.SaleDetail;
import pinodesk.viewmodel.SaleProductVM;
import pinodesk.viewmodel.SaleReportFilterVM;
import pinodesk.viewmodel.SaleReportVM;

public class SaleDetailRepositoryImpl extends AbstractRepository<SaleDetail> implements SaleDetailRepositoryCustom {

    @Override
    public List<SaleProductVM> findBySaleIdJoinProducts(Long saleId, String language) {
        String sql = """
                    select
                    a.*,
                    a.quantity as sale_quantity,
                    b.quantity as current_quantity,
                    b.name as product_name,
                    b.deleted_at as product_deleted_at,
                    f.label as product_unit_label,
                    b.general_selling_price,
                    b.prescription_selling_price,
                    c.code as product_category_code,
                    c.name as product_category_name,
                    e.expired_date,
                    e.batch_number
                from sale_detail a
                inner join product b on b.id = a.product_id
                inner join product_category c on c.code = b.category_code and c.language = ?
                inner join unit f on f.code = b.unit_code and f.language = ?
                left join product_expiry e on e.sale_detail_id = a.id
                where a.sale_id = ?
                """;
        return performSelect(sql, List.of(language, language, saleId), SaleProductVM.class);
    }

    @Override
    public List<SaleReportVM> findByFilter(SaleReportFilterVM filter, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                    select
                    sd.sale_id,
                    s.invoice_number,
                    s.invoice_date,
                    s.selling_mode,
                    c.name as customer_name,
                    p.name as product_name,
                    p.deleted_at as product_deleted_at,
                    sd.quantity,
                    u.label as unit,
                    sd.selling_price,
                    sd.subtotal,
                    s.total_product,
                    s.total_payment,
                    s.payment_status,
                    s.created_at
                from sale_detail sd
                inner join sale s on s.id = sd.sale_id
                left join customer c on c.id = s.customer_id
                inner join product p on p.id = sd.product_id
                inner join unit u on u.code = p.unit_code and u.language = ?
                """);
        Where where = new Where().isNull("sd.deleted_at").andIsNull("s.deleted_at");
        if (StringUtils.isNotBlank(filter.getInvoiceNumber())) {
            where.andContainsIgnoreCase("s.invoice_number", filter.getInvoiceNumber().trim());
        }
        if (StringUtils.isNotBlank(filter.getCustomerName())) {
            where.andContainsIgnoreCase("c.name", filter.getCustomerName().trim());
        }
        if (StringUtils.isNotBlank(filter.getProductName())) {
            where.andContainsIgnoreCase("p.name", filter.getProductName().trim());
        }
        if (filter.getInvoiceDateMin() != null) {
            where.andGreaterThanOrEqual("s.invoice_date", filter.getInvoiceDateMin());
        }
        if (filter.getInvoiceDateMax() != null) {
            where.andLowerThanOrEqual("s.invoice_date", filter.getInvoiceDateMax());
        }
        if (filter.getPaymentStatus() != null) {
            where.andEquals("s.payment_status", filter.getPaymentStatus().toString());
        }
        if (filter.getSellingMode() != null) {
            where.andEquals("s.selling_mode", filter.getSellingMode().toString());
        }
        sb.append(where.getClause());
        sb.append(" order by s.invoice_date, sd.id ");
        List<Object> values = where.getValues();
        values.add(0, language);
        return performSelect(sb.toString(), values, SaleReportVM.class);
    }
}
