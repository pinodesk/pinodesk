package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.Product;
import pinus.desktop.viewmodel.ProductFilterVM;
import pinus.desktop.viewmodel.ProductVM;
import pinus.desktop.viewmodel.SearchProductsByFilterVM;

public interface ProductRepository extends CommonRepository<Product> {

    List<ProductVM> filter(ProductFilterVM filter, String languageCode);

    List<SearchProductsByFilterVM> queryByFilter(ProductFilterVM filter, String languageCode);

    List<SearchProductsByFilterVM> findByKeyword(String keyword, String languageCode);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByBarcode(String barcode, Long... excludedIds);

    boolean existsByNameAndUnit(String name, Long unitId, Long... excludedIds);

}
