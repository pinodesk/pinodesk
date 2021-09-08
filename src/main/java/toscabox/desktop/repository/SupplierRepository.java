package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.Supplier;
import toscabox.desktop.viewmodel.SupplierAddVM;
import toscabox.desktop.viewmodel.SupplierEditVM;
import toscabox.desktop.viewmodel.SupplierFilterVM;

public interface SupplierRepository extends CommonRepository<Supplier> {

    List<Supplier> filter(SupplierFilterVM filter);

    List<Supplier> findByKeyword(String keyword);

    Long createSupplier(SupplierAddVM supplierAdd);

    Integer updateSupplier(SupplierEditVM supplierEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByEmail(String email, Long... excludeIds);

    boolean existsByPhone(String phone, Long... excludeIds);

    String findMaxCodeByPrefix(String prefix);
}
