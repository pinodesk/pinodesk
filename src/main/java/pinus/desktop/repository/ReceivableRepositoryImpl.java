package pinus.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.constant.PaymentStatus;
import pinus.desktop.domain.Receivable;
import pinus.desktop.viewmodel.ReceivableFilterVM;

public class ReceivableRepositoryImpl extends AbstractRepository<Receivable> implements ReceivableRepositoryCustom {

    @Override
    public List<Receivable> findByFilter(ReceivableFilterVM filter) {
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
        if (filter.getPaymentAmountMax() != null) {
            where.andLowerThanOrEqual(Receivable.C_PAYMENT_AMOUNT, filter.getPaymentAmountMax());
        }
        if (filter.getPaymentAmountMin() != null) {
            where.andGreaterThanOrEqual(Receivable.C_PAYMENT_AMOUNT, filter.getPaymentAmountMin());
        }
        if (filter.getPaymentDateMax() != null) {
            where.andLowerThanOrEqual(Receivable.C_PAYMENT_DATE, filter.getPaymentDateMax());
        }
        if (filter.getPaymentDateMin() != null) {
            where.andGreaterThanOrEqual(Receivable.C_PAYMENT_DATE, filter.getPaymentDateMin());
        }
        if (filter.getPaymentDueDateMax() != null) {
            where.andLowerThanOrEqual(Receivable.C_PAYMENT_DUE_DATE, filter.getPaymentDueDateMax());
        }
        if (filter.getPaymentDueDateMin() != null) {
            where.andGreaterThanOrEqual(Receivable.C_PAYMENT_DUE_DATE, filter.getPaymentDueDateMin());
        }
        if (StringUtils.isNotBlank(filter.getRemarks())) {
            where.containsIgnoreCase(Receivable.C_REMARKS, filter.getRemarks());
        }
        if (filter.getCustomerId() != null) {
            where.andEquals(Receivable.C_CUSTOMER_ID, filter.getCustomerId());
        }
        if (PaymentStatus.PAID.equals(filter.getPaymentStatus())) {
            where.isNotNull(Receivable.C_PAYMENT_DATE);
        } else if (PaymentStatus.UNPAID.equals(filter.getPaymentStatus())) {
            where.isNull(Receivable.C_PAYMENT_DATE);
        }
        return read(where);
    }

}
