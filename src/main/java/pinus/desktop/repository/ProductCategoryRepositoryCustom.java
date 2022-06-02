package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.ProductCategory;

public interface ProductCategoryRepositoryCustom {

    List<ProductCategory> findByKeyword(String keyword, String languageCode);

}
