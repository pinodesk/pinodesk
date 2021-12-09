package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Purchase;
import pinus.desktop.viewmodel.PurchaseFilterVM;
import pinus.desktop.viewmodel.PurchaseVM;

@Repository
public class PurchaseRepositoryImpl extends AbstractRepository<Purchase> implements PurchaseRepository {

    @Override
    public List<PurchaseVM> filter(PurchaseFilterVM filter) {
        StringBuilder sb = new StringBuilder();
        sb.append(
                " select a.*, b.id as supplier_id, b.name as supplier_name from purchase a inner join supplier b on b.id = a.supplier_id ");
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getOrderNumber())) {
            where.containsIgnoreCase("a.order_number", filter.getOrderNumber().trim());
        }
        if (filter.getOrderDateMin() != null) {
            where.andGreaterThanOrEqual("a.order_date", filter.getOrderDateMin());
        }
        if (filter.getOrderDateMax() != null) {
            where.andLowerThanOrEqual("a.order_date", filter.getOrderDateMax());
        }
        if (filter.getDueDateMin() != null) {
            where.andGreaterThanOrEqual("a.payment_due_date", filter.getDueDateMin());
        }
        if (filter.getDueDateMax() != null) {
            where.andLowerThanOrEqual("a.payment_due_date", filter.getOrderDateMax());
        }
        if (filter.getPaymentMethod() != null) {
            where.andEquals("a.payment_method", filter.getPaymentMethod().name());
        }
        if (filter.getPaymentPeriodUnit() != null) {
            where.andEquals("a.payment_period_unit", filter.getPaymentPeriodUnit().name());
        }
        if (filter.getPaymentStatus() != null) {
            where.andEquals("a.payment_status", filter.getPaymentStatus().name());
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
        List<Object> params = where.getValues();
        sb.append(params.isEmpty() ? " WHERE " : " AND ");
        sb.append(" a.deleted_at is null ");
        return performSelect(sb.toString(), params, PurchaseVM.class);
    }

    @Override
    public boolean existsByOrderNumberAndSupplierId(String orderNumber, Long supplierId) {
        return exists(
                new Where().equals(Purchase.C_ORDER_NUMBER, orderNumber).andEquals(Purchase.C_SUPPLIER_ID, supplierId));
    }
}
