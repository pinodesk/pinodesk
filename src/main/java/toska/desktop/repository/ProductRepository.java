package toska.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toska.desktop.domain.Product;
import toska.desktop.viewmodel.ProductAddVM;
import toska.desktop.viewmodel.ProductEditVM;
import toska.desktop.viewmodel.ProductFilterVM;
import toska.desktop.viewmodel.ProductVM;

public interface ProductRepository extends CommonRepository<Product> {
    
    List<ProductVM> filter(ProductFilterVM filter, String languageCode);

    Integer updateProduct(ProductEditVM productEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByBarcode(String barcode, Long... excludedIds);

    boolean existsByNameAndUnit(String name, Long unitId, Long... excludedIds);

    Long createProduct(ProductAddVM productAdd);

}
