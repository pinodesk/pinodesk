package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.Supplier;
import pinus.desktop.viewmodel.SupplierFilterVM;

public interface SupplierRepositoryCustom {

    List<Supplier> findByFilter(SupplierFilterVM filter);

    List<Supplier> findByKeyword(String keyword);

}
