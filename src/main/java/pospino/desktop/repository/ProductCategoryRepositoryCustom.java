package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.domain.ProductCategory;

public interface ProductCategoryRepositoryCustom {

    List<ProductCategory> findByKeyword(String keyword, String language);

}
