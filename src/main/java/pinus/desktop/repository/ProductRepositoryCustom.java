package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.ProductFilterVM;
import pinus.desktop.viewmodel.ProductVM;

public interface ProductRepositoryCustom {

    List<ProductVM> findByFilter(ProductFilterVM filter, String languageCode);

    List<ProductVM> findByKeyword(String keyword, String languageCode);

}
