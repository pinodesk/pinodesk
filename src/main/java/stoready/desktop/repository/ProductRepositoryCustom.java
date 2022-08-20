package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.ProductFilterVM;
import stoready.desktop.viewmodel.ProductVM;

public interface ProductRepositoryCustom {

    List<ProductVM> findByFilter(ProductFilterVM filter, String language);

    List<ProductVM> findByKeyword(String keyword, String language);

}
