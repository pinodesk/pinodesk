package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.Product;
import tosca.desktop.viewmodel.ProductAddVM;
import tosca.desktop.viewmodel.ProductEditVM;
import tosca.desktop.viewmodel.ProductFilterVM;
import tosca.desktop.viewmodel.ProductVM;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface ProductRepository extends CommonRepository<Product> {
    
    List<ProductVM> filter(ProductFilterVM filter, String languageCode);

    Integer updateProduct(ProductEditVM productEdit);

    boolean existsByCode(String code, Long... excludedIds);

    boolean existsByBarcode(String barcode, Long... excludedIds);

    boolean existsByNameAndUnit(String name, Long unitId, Long... excludedIds);

    Long createProduct(ProductAddVM productAdd);

}
