package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.Supplier;
import tosca.desktop.viewmodel.SupplierAddVM;
import tosca.desktop.viewmodel.SupplierEditVM;
import tosca.desktop.viewmodel.SupplierFilterVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface SupplierRepository extends CommonRepository<Supplier> {
    
	List<Supplier> filter(SupplierFilterVM filter);
    
    Long createSupplier(SupplierAddVM supplierAdd);

    Integer updateSupplier(SupplierEditVM supplierEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByEmail(String email, Long... excludeIds);

    boolean existsByPhone(String phone, Long... excludeIds);

    String findMaxCodeByPrefix(String prefix);
}
