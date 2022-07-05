package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.lang3.StringUtils;

import pinus.desktop.domain.Sale;
import pinus.desktop.viewmodel.SaleFilterVM;
import pinus.desktop.viewmodel.SaleVM;

public class SaleRepositoryImpl extends AbstractRepository<Sale> implements SaleRepositoryCustom {

    @Override
    public List<SaleVM> findByFilter(SaleFilterVM filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select
                    a.*,
                    b.id as customer_id,
                    b.name as customer_name,
                    c.id as doctor_id,
                    c.name as doctor_name
                from sale a
                left join customer b on b.id = a.customer_id
                left join doctor c on c.id = a.doctor_id
                """);
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getInvoiceNumber())) {
            where.containsIgnoreCase("a.invoice_number", filter.getInvoiceNumber().trim());
        }
        if (filter.getCreatedDateMin() != null) {
            where.andGreaterThanOrEqual("a.created_at", filter.getCreatedDateMin());
        }
        if (filter.getCreatedDateMax() != null) {
            where.andLowerThanOrEqual("a.created_at", filter.getCreatedDateMax());
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
        if (filter.getCustomerId() != null) {
            where.andEquals("a.customer_id", filter.getCustomerId());
        }
        if (filter.getDoctorId() != null) {
            where.andEquals("a.doctor_id", filter.getDoctorId());
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
        List<Object> params = where.getValues();
        sb.append(params.isEmpty() ? " WHERE " : " AND ");
        sb.append(" a.deleted_at is null ");
        return performSelect(sb.toString(), params, SaleVM.class);
    }

}
