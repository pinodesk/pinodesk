package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.Product;
import toscabox.desktop.viewmodel.ProductAddVM;
import toscabox.desktop.viewmodel.ProductEditVM;
import toscabox.desktop.viewmodel.ProductFilterVM;
import toscabox.desktop.viewmodel.ProductVM;

public interface ProductRepository extends CommonRepository<Product> {
    
    List<ProductVM> filter(ProductFilterVM filter, String languageCode);

    Integer updateProduct(ProductEditVM productEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByBarcode(String barcode, Long... excludedIds);

    boolean existsByNameAndUnit(String name, Long unitId, Long... excludedIds);

    Long createProduct(ProductAddVM productAdd);

}
