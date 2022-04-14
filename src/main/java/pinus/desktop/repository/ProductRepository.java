package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.Product;
import pinus.desktop.viewmodel.ProductFilterVM;
import pinus.desktop.viewmodel.ProductVM;

public interface ProductRepository extends CommonRepository<Product> {

    List<ProductVM> findByFilter(ProductFilterVM filter, String languageCode);

    List<ProductVM> findByKeyword(String keyword, String languageCode);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByBarcode(String barcode, Long... excludedIds);

    boolean existsByNameAndUnit(String name, Long unitId, Long... excludedIds);

}
