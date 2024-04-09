package pinodesk.repository;

import java.util.List;

import com.mudiatech.sequel.repository.AbstractRepository;
import com.mudiatech.sequel.sql.Where;

import pinodesk.domain.Sale;
import pinodesk.viewmodel.SaleFilterVM;
import pinodesk.viewmodel.SaleVM;

import org.apache.commons.lang3.StringUtils;

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
                    c.name as doctor_name,
                    d.full_name as user_full_name
                from sale a
                left join customer b on b.id = a.customer_id
                left join doctor c on c.id = a.doctor_id
                inner join `user` d on d.id = a.user_id
                """);
        Where where = new Where().isNull("a.deleted_at");
        if (StringUtils.isNotBlank(filter.getInvoiceNumber())) {
            where.andContainsIgnoreCase("a.invoice_number", filter.getInvoiceNumber().trim());
        }
        if (filter.getCreatedDateMin() != null) {
            where.andGreaterThanOrEqual("a.created_at", filter.getCreatedDateMin());
        }
        if (filter.getCreatedDateMax() != null) {
            where.andLowerThanOrEqual("a.created_at", filter.getCreatedDateMax().atTime(23, 59, 59));
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
        return performSelect(sb.toString(), where.getValues(), SaleVM.class);
    }

}
