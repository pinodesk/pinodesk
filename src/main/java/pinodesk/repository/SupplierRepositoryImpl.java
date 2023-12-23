package pinodesk.repository;

import java.util.List;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;
import com.gitlab.mudiasoft.sequel.sql.Where;

import pinodesk.domain.Supplier;
import pinodesk.viewmodel.SupplierFilterVM;

import org.apache.commons.lang3.StringUtils;

public class SupplierRepositoryImpl extends AbstractRepository<Supplier> implements SupplierRepositoryCustom {

    @Override
    public List<Supplier> findByFilter(SupplierFilterVM filter) {
        Where where = new Where();
        if (StringUtils.isNotBlank(filter.getName())) {
            where.containsIgnoreCase(Supplier.C_NAME, filter.getName());
        }
        if (StringUtils.isNotBlank(filter.getCode())) {
            where.contains(Supplier.C_CODE, filter.getCode());
        }
        if (StringUtils.isNotBlank(filter.getPhone())) {
            where.contains(Supplier.C_PHONE, filter.getPhone());
        }
        if (StringUtils.isNotBlank(filter.getEmail())) {
            where.containsIgnoreCase(Supplier.C_EMAIL, filter.getEmail());
        }
        if (StringUtils.isNotBlank(filter.getWebsite())) {
            where.containsIgnoreCase(Supplier.C_WEBSITE, filter.getWebsite());
        }
        if (StringUtils.isNotBlank(filter.getAddress())) {
            where.containsIgnoreCase(Supplier.C_ADDRESS, filter.getAddress());
        }
        return read(where);
    }

    @Override
    public List<Supplier> findByKeyword(String keyword) {
        Where where = new Where().containsIgnoreCase(Supplier.C_NAME, keyword)
                .orContainsIgnoreCase(Supplier.C_EMAIL, keyword).orContains(Supplier.C_CODE, keyword)
                .orContains(Supplier.C_PHONE, keyword).orContainsIgnoreCase(Supplier.C_WEBSITE, keyword)
                .orContainsIgnoreCase(Supplier.C_ADDRESS, keyword);
        return read(where);
    }

}
