package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.domain.ProductCategory;

public interface ProductCategoryRepositoryCustom {

    List<ProductCategory> findByKeyword(String keyword, String language);

}
