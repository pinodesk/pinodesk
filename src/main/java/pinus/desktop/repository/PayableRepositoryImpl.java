package pinus.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.constant.PaymentStatus;
import pinus.desktop.domain.Payable;
import pinus.desktop.viewmodel.PayableFilterVM;

public class PayableRepositoryImpl extends AbstractRepository<Payable> implements PayableRepositoryCustom {

    @Override
    public List<Payable> findByFilter(PayableFilterVM filter) {
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
        if (filter.getPaymentAmountMax() != null) {
            where.andLowerThanOrEqual(Payable.C_PAYMENT_AMOUNT, filter.getPaymentAmountMax());
        }
        if (filter.getPaymentAmountMin() != null) {
            where.andGreaterThanOrEqual(Payable.C_PAYMENT_AMOUNT, filter.getPaymentAmountMin());
        }
        if (filter.getPaymentDateMax() != null) {
            where.andLowerThanOrEqual(Payable.C_PAYMENT_DATE, filter.getPaymentDateMax());
        }
        if (filter.getPaymentDateMin() != null) {
            where.andGreaterThanOrEqual(Payable.C_PAYMENT_DATE, filter.getPaymentDateMin());
        }
        if (filter.getPaymentDueDateMax() != null) {
            where.andLowerThanOrEqual(Payable.C_PAYMENT_DUE_DATE, filter.getPaymentDueDateMax());
        }
        if (filter.getPaymentDueDateMin() != null) {
            where.andGreaterThanOrEqual(Payable.C_PAYMENT_DUE_DATE, filter.getPaymentDueDateMin());
        }
        if (StringUtils.isNotBlank(filter.getRemarks())) {
            where.containsIgnoreCase(Payable.C_REMARKS, filter.getRemarks());
        }
        if (filter.getSupplierId() != null) {
            where.andEquals(Payable.C_SUPPLIER_ID, filter.getSupplierId());
        }
        if (PaymentStatus.PAID.equals(filter.getPaymentStatus())) {
            where.isNotNull(Payable.C_PAYMENT_DATE);
        } else if (PaymentStatus.UNPAID.equals(filter.getPaymentStatus())) {
            where.isNull(Payable.C_PAYMENT_DATE);
        }
        return read(where);
    }

}
