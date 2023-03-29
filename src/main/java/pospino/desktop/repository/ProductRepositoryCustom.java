package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.ProductFilterVM;
import pospino.desktop.viewmodel.ProductVM;

public interface ProductRepositoryCustom {

    List<ProductVM> findByFilter(ProductFilterVM filter, String language);

    List<ProductVM> findByKeyword(String keyword, String language);

}
