package tosca.desktop.repository;

import tosca.desktop.domain.SupplierContact;
import tosca.desktop.viewmodel.SupplierContactAddVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface SupplierContactRepository extends CommonRepository<SupplierContact> {
    
    Long createSupplierContact(SupplierContactAddVM supplierContact);

    boolean existsByEmailAndSupplierId(String email, Long supplierId, Long... excludeIds);

    boolean existsByPhoneAndSupplierId(String phone, Long supplierId, Long... excludeIds);

}
