package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.domain.Supplier;
import stoready.desktop.viewmodel.SupplierFilterVM;

public interface SupplierRepositoryCustom {

    List<Supplier> findByFilter(SupplierFilterVM filter);

    List<Supplier> findByKeyword(String keyword);

}
