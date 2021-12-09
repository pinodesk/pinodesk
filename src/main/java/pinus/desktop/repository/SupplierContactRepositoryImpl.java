package pinus.desktop.repository;

import java.util.Arrays;

import com.gitlab.muhammadkholidb.sequel.model.DataModel;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.SupplierContact;
import pinus.desktop.viewmodel.SupplierContactAddVM;

@Repository
public class SupplierContactRepositoryImpl extends AbstractRepository<SupplierContact>
        implements SupplierContactRepository {

    @Override
    public Long createSupplierContact(SupplierContactAddVM supplierContact) {
        return insert(
                new String[] {
                        SupplierContact.C_SUPPLIER_ID,
                        SupplierContact.C_NAME,
                        SupplierContact.C_PHONE,
                        SupplierContact.C_EMAIL },
                new Object[] {
                        supplierContact.getSupplierId(),
                        supplierContact.getName(),
                        supplierContact.getPhone(),
                        supplierContact.getEmail() });
    }

    @Override
    public boolean existsByEmailAndSupplierId(String email, Long supplierId, Long... excludeIds) {
        Where where = new Where().equalsIgnoreCase(SupplierContact.C_EMAIL, email)
                .andEquals(SupplierContact.C_SUPPLIER_ID, supplierId);
        if (ArrayUtils.isNotEmpty(excludeIds)) {
            where.andNotIn(DataModel.C_ID, Arrays.asList(excludeIds));
        }
        return exists(where);
    }

    @Override
    public boolean existsByPhoneAndSupplierId(String phone, Long supplierId, Long... excludeIds) {
        Where where = new Where().equalsIgnoreCase(SupplierContact.C_PHONE, phone)
                .andEquals(SupplierContact.C_SUPPLIER_ID, supplierId);
        if (ArrayUtils.isNotEmpty(excludeIds)) {
            where.andNotIn(DataModel.C_ID, Arrays.asList(excludeIds));
        }
        return exists(where);
    }

}
