package pospino.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;
import com.gitlab.mudiasoft.sequel.sql.Where;

import pospino.desktop.constant.PaymentStatus;
import pospino.desktop.domain.Receivable;
import pospino.desktop.viewmodel.ReceivableFilterVM;
import pospino.desktop.viewmodel.ReceivableVM;

public class ReceivableRepositoryImpl extends AbstractRepository<Receivable> implements ReceivableRepositoryCustom {

    @Override
    public List<ReceivableVM> findByFilter(ReceivableFilterVM filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select a.*, b.id as customer_id, b.name as customer_name
                from receivable a
                inner join customer b on b.id = a.customer_id
                """);
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getInvoiceNumber())) {
            where.containsIgnoreCase(Receivable.C_INVOICE_NUMBER, filter.getInvoiceNumber());
        }
        if (filter.getInvoiceDateMax() != null) {
            where.andLowerThanOrEqual(Receivable.C_INVOICE_DATE, filter.getInvoiceDateMax());
        }
        if (filter.getInvoiceDateMin() != null) {
            where.andGreaterThanOrEqual(Receivable.C_INVOICE_DATE, filter.getInvoiceDateMin());
        }
        if (filter.getAmountMax() != null) {
            where.andLowerThanOrEqual(Receivable.C_AMOUNT, filter.getAmountMax());
        }
        if (filter.getAmountMin() != null) {
            where.andGreaterThanOrEqual(Receivable.C_AMOUNT, filter.getAmountMin());
        }
        if (filter.getCompletionDateMax() != null) {
            where.andLowerThanOrEqual(Receivable.C_COMPLETION_DATE, filter.getCompletionDateMax());
        }
        if (filter.getCompletionDateMin() != null) {
            where.andGreaterThanOrEqual(Receivable.C_COMPLETION_DATE, filter.getCompletionDateMin());
        }
        if (filter.getDueDateMax() != null) {
            where.andLowerThanOrEqual(Receivable.C_DUE_DATE, filter.getDueDateMax());
        }
        if (filter.getDueDateMin() != null) {
            where.andGreaterThanOrEqual(Receivable.C_DUE_DATE, filter.getDueDateMin());
        }
        if (StringUtils.isNotBlank(filter.getRemarks())) {
            where.containsIgnoreCase(Receivable.C_REMARKS, filter.getRemarks());
        }
        if (filter.getCustomerId() != null) {
            where.andEquals(Receivable.C_CUSTOMER_ID, filter.getCustomerId());
        }
        if (PaymentStatus.PAID.equals(filter.getPaymentStatus())) {
            where.isNotNull(Receivable.C_COMPLETION_DATE);
        } else if (PaymentStatus.UNPAID.equals(filter.getPaymentStatus())) {
            where.isNull(Receivable.C_COMPLETION_DATE);
        }
        sb.append(where.getClause());
        List<Object> params = where.getValues();
        sb.append(params.isEmpty() ? " WHERE " : " AND ");
        sb.append(" a.deleted_at is null ");
        return performSelect(sb.toString(), params, ReceivableVM.class);
    }

}
