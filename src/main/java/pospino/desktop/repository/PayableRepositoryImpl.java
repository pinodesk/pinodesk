package pospino.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;
import com.gitlab.mudiasoft.sequel.sql.Where;

import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.domain.Payable;
import pospino.desktop.viewmodel.PayableFilterVM;
import pospino.desktop.viewmodel.PayableVM;

public class PayableRepositoryImpl extends AbstractRepository<Payable> implements PayableRepositoryCustom {

    @Override
    public List<PayableVM> findByFilter(PayableFilterVM filter) {
        String colCompletionDate = "a.completion_date";
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select a.*, b.id as supplier_id, b.name as supplier_name
                from payable a
                inner join supplier b on b.id = a.supplier_id
                """);
        Where where = new Where().isNull("a.deleted_at");
        if (StringUtils.isNotBlank(filter.getInvoiceNumber())) {
            where.andContainsIgnoreCase("a.invoice_number", filter.getInvoiceNumber());
        }
        if (filter.getInvoiceDateMax() != null) {
            where.andLowerThanOrEqual("a.invoice_date", filter.getInvoiceDateMax());
        }
        if (filter.getInvoiceDateMin() != null) {
            where.andGreaterThanOrEqual("a.invoice_date", filter.getInvoiceDateMin());
        }
        if (filter.getAmountMax() != null) {
            where.andLowerThanOrEqual("a.amount", filter.getAmountMax());
        }
        if (filter.getAmountMin() != null) {
            where.andGreaterThanOrEqual("a.amount", filter.getAmountMin());
        }
        if (filter.getCompletionDateMax() != null) {
            where.andLowerThanOrEqual(colCompletionDate, filter.getCompletionDateMax());
        }
        if (filter.getCompletionDateMin() != null) {
            where.andGreaterThanOrEqual(colCompletionDate, filter.getCompletionDateMin());
        }
        if (filter.getDueDateMax() != null) {
            where.andLowerThanOrEqual("a.due_date", filter.getDueDateMax());
        }
        if (filter.getDueDateMin() != null) {
            where.andGreaterThanOrEqual("a.due_date", filter.getDueDateMin());
        }
        if (StringUtils.isNotBlank(filter.getRemarks())) {
            where.containsIgnoreCase("a.remarks", filter.getRemarks());
        }
        if (filter.getSupplierId() != null) {
            where.andEquals("a.supplier_id", filter.getSupplierId());
        }
        if (PaymentStatus.PAID.equals(filter.getPaymentStatus())) {
            where.isNotNull(colCompletionDate);
        } else if (PaymentStatus.UNPAID.equals(filter.getPaymentStatus())) {
            where.isNull(colCompletionDate);
        }
        sb.append(where.getClause());
        return performSelect(sb.toString(), where.getValues(), PayableVM.class);
    }

}
