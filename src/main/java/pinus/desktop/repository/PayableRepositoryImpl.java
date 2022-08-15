package pinus.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.constant.PaymentStatus;
import pinus.desktop.domain.Payable;
import pinus.desktop.viewmodel.PayableFilterVM;
import pinus.desktop.viewmodel.PayableVM;

public class PayableRepositoryImpl extends AbstractRepository<Payable> implements PayableRepositoryCustom {

    @Override
    public List<PayableVM> findByFilter(PayableFilterVM filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select a.*, b.id as supplier_id, b.name as supplier_name
                from payable a
                inner join supplier b on b.id = a.supplier_id
                """);
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getInvoiceNumber())) {
            where.containsIgnoreCase(Payable.C_INVOICE_NUMBER, filter.getInvoiceNumber());
        }
        if (filter.getInvoiceDateMax() != null) {
            where.andLowerThanOrEqual(Payable.C_INVOICE_DATE, filter.getInvoiceDateMax());
        }
        if (filter.getInvoiceDateMin() != null) {
            where.andGreaterThanOrEqual(Payable.C_INVOICE_DATE, filter.getInvoiceDateMin());
        }
        if (filter.getAmountMax() != null) {
            where.andLowerThanOrEqual(Payable.C_AMOUNT, filter.getAmountMax());
        }
        if (filter.getAmountMin() != null) {
            where.andGreaterThanOrEqual(Payable.C_AMOUNT, filter.getAmountMin());
        }
        if (filter.getCompletionDateMax() != null) {
            where.andLowerThanOrEqual(Payable.C_COMPLETION_DATE, filter.getCompletionDateMax());
        }
        if (filter.getCompletionDateMin() != null) {
            where.andGreaterThanOrEqual(Payable.C_COMPLETION_DATE, filter.getCompletionDateMin());
        }
        if (filter.getDueDateMax() != null) {
            where.andLowerThanOrEqual(Payable.C_DUE_DATE, filter.getDueDateMax());
        }
        if (filter.getDueDateMin() != null) {
            where.andGreaterThanOrEqual(Payable.C_DUE_DATE, filter.getDueDateMin());
        }
        if (StringUtils.isNotBlank(filter.getRemarks())) {
            where.containsIgnoreCase(Payable.C_REMARKS, filter.getRemarks());
        }
        if (filter.getSupplierId() != null) {
            where.andEquals(Payable.C_SUPPLIER_ID, filter.getSupplierId());
        }
        if (PaymentStatus.PAID.equals(filter.getPaymentStatus())) {
            where.isNotNull(Payable.C_COMPLETION_DATE);
        } else if (PaymentStatus.UNPAID.equals(filter.getPaymentStatus())) {
            where.isNull(Payable.C_COMPLETION_DATE);
        }
        sb.append(where.getClause());
        List<Object> params = where.getValues();
        sb.append(params.isEmpty() ? " WHERE " : " AND ");
        sb.append(" a.deleted_at is null ");
        return performSelect(sb.toString(), params, PayableVM.class);
    }

}
