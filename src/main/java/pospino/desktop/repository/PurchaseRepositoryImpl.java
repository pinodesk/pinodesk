package pospino.desktop.repository;

import java.util.List;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;
import com.gitlab.mudiasoft.sequel.sql.Where;

import pospino.desktop.domain.Purchase;
import pospino.desktop.viewmodel.PurchaseFilterVM;
import pospino.desktop.viewmodel.PurchaseVM;

import org.apache.commons.lang3.StringUtils;

public class PurchaseRepositoryImpl extends AbstractRepository<Purchase> implements PurchaseRepositoryCustom {

    @Override
    public List<PurchaseVM> findByFilter(PurchaseFilterVM filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select a.*, b.id as supplier_id, b.name as supplier_name
                from purchase a
                inner join supplier b on b.id = a.supplier_id
                """);
        Where where = new Where().isNull("a.deleted_at");
        if (StringUtils.isNotBlank(filter.getInvoiceNumber())) {
            where.andContainsIgnoreCase("a.invoice_number", filter.getInvoiceNumber().trim());
        }
        if (filter.getInvoiceDateMin() != null) {
            where.andGreaterThanOrEqual("a.invoice_date", filter.getInvoiceDateMin());
        }
        if (filter.getInvoiceDateMax() != null) {
            where.andLowerThanOrEqual("a.invoice_date", filter.getInvoiceDateMax());
        }
        if (filter.getDueDateMin() != null) {
            where.andGreaterThanOrEqual("a.payment_due_date", filter.getDueDateMin());
        }
        if (filter.getDueDateMax() != null) {
            where.andLowerThanOrEqual("a.payment_due_date", filter.getDueDateMax());
        }
        if (filter.getPaymentStatus() != null) {
            where.andEquals("a.payment_status", filter.getPaymentStatus().toString());
        }
        if (filter.getSupplierId() != null) {
            where.andEquals("a.supplier_id", filter.getSupplierId());
        }
        if (filter.getTotalPaymentMax() != null) {
            where.andLowerThanOrEqual("a.total_payment", filter.getTotalPaymentMax());
        }
        if (filter.getTotalPaymentMin() != null) {
            where.andGreaterThanOrEqual("a.total_payment", filter.getTotalPaymentMin());
        }
        if (filter.getTotalProductMax() != null) {
            where.andLowerThanOrEqual("a.total_product", filter.getTotalProductMax());
        }
        if (filter.getTotalProductMin() != null) {
            where.andGreaterThanOrEqual("a.total_product", filter.getTotalProductMin());
        }
        sb.append(where.getClause());
        return performSelect(sb.toString(), where.getValues(), PurchaseVM.class);
    }

}
