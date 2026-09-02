package pinodesk.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pinodesk.sequel.repository.AbstractRepository;
import com.pinodesk.sequel.sql.Where;

import pinodesk.entity.Consignment;
import pinodesk.viewmodel.ConsignmentFilterVM;
import pinodesk.viewmodel.ConsignmentVM;

public class ConsignmentRepositoryImpl extends AbstractRepository<Consignment> implements ConsignmentRepositoryCustom {

    @Override
    public List<ConsignmentVM> findByFilter(ConsignmentFilterVM filter) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select a.*,
                    b.id as supplier_id,
                    b.name as supplier_name,
                    c.full_name as user_full_name
                from consignment a
                inner join supplier b on b.id = a.supplier_id
                inner join `user` c on c.id = a.user_id
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
        if (filter.getSupplierId() != null) {
            where.andEquals("a.supplier_id", filter.getSupplierId());
        }
        sb.append(where.getClause());
        return performSelect(sb.toString(), where.getValues(), ConsignmentVM.class);
    }
}