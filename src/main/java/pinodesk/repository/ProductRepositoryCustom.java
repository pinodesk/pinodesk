package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.ProductFilterVM;
import pinodesk.viewmodel.ProductVM;

public interface ProductRepositoryCustom {

    List<ProductVM> findByFilter(ProductFilterVM filter, String language);

    List<ProductVM> findByKeyword(String keyword, String language);

}
