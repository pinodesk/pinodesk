package pinus.desktop.repository;

import java.util.Arrays;
import java.util.List;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Order.Direction;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Supplier;
import pinus.desktop.viewmodel.SupplierAddVM;
import pinus.desktop.viewmodel.SupplierEditVM;
import pinus.desktop.viewmodel.SupplierFilterVM;

@Repository
public class SupplierRepositoryImpl extends AbstractRepository<Supplier> implements SupplierRepository {

    @Override
    public List<Supplier> filter(SupplierFilterVM filter) {
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
        if (StringUtils.isNotBlank(filter.getType())) {
            where.equals(Supplier.C_TYPE, filter.getType());
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

    @Override
    public Long createSupplier(SupplierAddVM supplierAdd) {
        return insert(
                new String[] {
                        Supplier.C_NAME,
                        Supplier.C_CODE,
                        Supplier.C_PHONE,
                        Supplier.C_EMAIL,
                        Supplier.C_WEBSITE,
                        Supplier.C_ADDRESS,
                        Supplier.C_TYPE },
                new Object[] {
                        supplierAdd.getName(),
                        supplierAdd.getCode(),
                        supplierAdd.getPhone(),
                        supplierAdd.getEmail(),
                        supplierAdd.getWebsite(),
                        supplierAdd.getAddress(),
                        supplierAdd.getType() });
    }

    @Override
    public Integer updateSupplier(SupplierEditVM supplierEdit) {
        return update(
                new String[] {
                        Supplier.C_NAME,
                        Supplier.C_CODE,
                        Supplier.C_PHONE,
                        Supplier.C_EMAIL,
                        Supplier.C_WEBSITE,
                        Supplier.C_ADDRESS,
                        Supplier.C_TYPE },
                new Object[] {
                        supplierEdit.getName(),
                        supplierEdit.getCode(),
                        supplierEdit.getPhone(),
                        supplierEdit.getEmail(),
                        supplierEdit.getWebsite(),
                        supplierEdit.getAddress(),
                        supplierEdit.getType() },
                supplierEdit.getId());
    }

    @Override
    public boolean existsByCode(String code, Long... excludedIds) {
        Where where = new Where().equalsIgnoreCase(Supplier.C_CODE, code);
        if (ArrayUtils.isNotEmpty(excludedIds)) {
            where.andNotIn(DataModel.C_ID, Arrays.asList(excludedIds));
        }
        return exists(where);
    }

    @Override
    public boolean existsByEmail(String email, Long... excludeIds) {
        Where where = new Where().equalsIgnoreCase(Supplier.C_EMAIL, email);
        if (ArrayUtils.isNotEmpty(excludeIds)) {
            where.andNotIn(DataModel.C_ID, Arrays.asList(excludeIds));
        }
        return exists(where);
    }

    @Override
    public boolean existsByPhone(String phone, Long... excludeIds) {
        Where where = new Where().equalsIgnoreCase(Supplier.C_PHONE, phone);
        if (ArrayUtils.isNotEmpty(excludeIds)) {
            where.andNotIn(DataModel.C_ID, Arrays.asList(excludeIds));
        }
        return exists(where);
    }

    @Override
    public String findMaxCodeByPrefix(String prefix) {
        return readOne(
                new Where().startsWith(Supplier.C_CODE, prefix),
                new Order().by(Supplier.C_CODE, Direction.DESCENDING),
                true).map(Supplier::getCode).orElse(null);
    }

}
