package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.domain.Supplier;
import pospino.desktop.viewmodel.SupplierFilterVM;

public interface SupplierRepositoryCustom {

    List<Supplier> findByFilter(SupplierFilterVM filter);

    List<Supplier> findByKeyword(String keyword);

}
