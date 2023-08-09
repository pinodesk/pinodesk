package pospino.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;
import com.gitlab.mudiasoft.sequel.sql.Where;

import pospino.desktop.domain.PurchaseDetail;
import pospino.desktop.viewmodel.PurchaseProductVM;
import pospino.desktop.viewmodel.PurchaseReportFilterVM;
import pospino.desktop.viewmodel.PurchaseReportVM;

public class PurchaseDetailRepositoryImpl extends AbstractRepository<PurchaseDetail>
        implements PurchaseDetailRepositoryCustom {

    @Override
    public List<PurchaseProductVM> findByPurchaseIdJoinProducts(Long purchaseId, String language) {
        String sql = """
                    select
                    a.*,
                    b.name as product_name ,
                    b.unit_label as product_unit_label,
                    c.code as product_category_code,
                    c.name as product_category_name,
                    d.general_selling_price,
                    d.prescription_selling_price,
                    e.expired_date,
                    e.batch_number
                from purchase_detail a
                inner join product b on b.id = a.product_id
                inner join product_category c on c.code = b.category_code and c.language = ?
                inner join product_price d on d.id = (
                    select f.id from product_price f where f.purchase_id = a.purchase_id and f.product_id = a.product_id order by f.id desc limit 1)
                left join product_expiry e on e.id = (
                    select g.id from product_expiry g where g.purchase_id = a.purchase_id and g.product_id = a.product_id order by g.id desc limit 1)
                where a.purchase_id = ?
                """;
        return performSelect(sql, List.of(language, purchaseId), PurchaseProductVM.class);
    }

    @Override
    public List<PurchaseReportVM> findByFilter(PurchaseReportFilterVM filter, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                    select
                    a.purchase_id,
                    b.invoice_number,
                    b.invoice_date,
                    c.name as supplier_name,
                    d.name as product_name,
                    a.quantity,
                    d.unit_label as unit,
                    a.buying_price,
                    a.buying_price_discount,
                    a.discount_type,
                    a.discount_amount,
                    a.subtotal_price,
                    a.subtotal_discount,
                    b.total_product,
                    b.total_payment,
                    b.payment_status,
                    b.created_at
                from purchase_detail a
                inner join purchase b on b.id = a.purchase_id
                left join supplier c on c.id = b.supplier_id
                inner join product d on d.id = a.product_id
                """);
        Where where = new Where().isNull("a.deleted_at").andIsNull("b.deleted_at");
        if (StringUtils.isNotBlank(filter.getInvoiceNumber())) {
            where.andContainsIgnoreCase("b.invoice_number", filter.getInvoiceNumber().trim());
        }
        if (StringUtils.isNotBlank(filter.getSupplierName())) {
            where.andContainsIgnoreCase("c.name", filter.getSupplierName().trim());
        }
        if (StringUtils.isNotBlank(filter.getProductName())) {
            where.andContainsIgnoreCase("d.name", filter.getProductName().trim());
        }
        if (filter.getInvoiceDateMin() != null) {
            where.andGreaterThanOrEqual("b.invoice_date", filter.getInvoiceDateMin());
        }
        if (filter.getInvoiceDateMax() != null) {
            where.andLowerThan("b.invoice_date", filter.getInvoiceDateMax().plusDays(1));
        }
        if (filter.getPaymentStatus() != null) {
            where.andEquals("b.payment_status", filter.getPaymentStatus().toString());
        }
        sb.append(where.getClause());
        sb.append(" order by b.invoice_date, a.id ");
        return performSelect(sb.toString(), where.getValues(), PurchaseReportVM.class);
    }
}
