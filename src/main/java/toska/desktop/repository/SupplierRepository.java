package toska.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toska.desktop.domain.Supplier;
import toska.desktop.viewmodel.SupplierAddVM;
import toska.desktop.viewmodel.SupplierEditVM;
import toska.desktop.viewmodel.SupplierFilterVM;

public interface SupplierRepository extends CommonRepository<Supplier> {
    
	List<Supplier> filter(SupplierFilterVM filter);
    
    Long createSupplier(SupplierAddVM supplierAdd);

    Integer updateSupplier(SupplierEditVM supplierEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByEmail(String email, Long... excludeIds);

    boolean existsByPhone(String phone, Long... excludeIds);

    String findMaxCodeByPrefix(String prefix);
}
