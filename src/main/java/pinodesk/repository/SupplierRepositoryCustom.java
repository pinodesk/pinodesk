package pinodesk.repository;

import java.util.List;

import pinodesk.entity.Supplier;
import pinodesk.viewmodel.SupplierFilterVM;

public interface SupplierRepositoryCustom {

    List<Supplier> findByFilter(SupplierFilterVM filter);

    List<Supplier> findByKeyword(String keyword);

}
