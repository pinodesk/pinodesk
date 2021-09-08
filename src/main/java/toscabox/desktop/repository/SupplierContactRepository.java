package toscabox.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.SupplierContact;
import toscabox.desktop.viewmodel.SupplierContactAddVM;

public interface SupplierContactRepository extends CommonRepository<SupplierContact> {

    Long createSupplierContact(SupplierContactAddVM supplierContact);

    boolean existsByEmailAndSupplierId(String email, Long supplierId, Long... excludeIds);

    boolean existsByPhoneAndSupplierId(String phone, Long supplierId, Long... excludeIds);

}
