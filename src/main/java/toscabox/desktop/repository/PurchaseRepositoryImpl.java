package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import toscabox.desktop.domain.Purchase;
import toscabox.desktop.viewmodel.PurchaseFilterVM;
import toscabox.desktop.viewmodel.PurchaseVM;

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
        sb.append(where.getClause());
        List<Object> params = where.getValues();
        sb.append(params.isEmpty() ? " WHERE " : " AND ");
        sb.append(" a.deleted_at is null and b.deleted_at is null");
        return performSelect(sb.toString(), params, PurchaseVM.class);
    }

}
