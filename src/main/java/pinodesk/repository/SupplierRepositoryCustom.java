package pinodesk.repository;

import java.util.List;

import pinodesk.domain.Supplier;
import pinodesk.viewmodel.SupplierFilterVM;

public interface SupplierRepositoryCustom {

    List<Supplier> findByFilter(SupplierFilterVM filter);

    List<Supplier> findByKeyword(String keyword);

}
