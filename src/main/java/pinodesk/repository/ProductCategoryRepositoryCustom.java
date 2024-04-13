package pinodesk.repository;

import java.util.List;

import pinodesk.domain.ProductCategory;

public interface ProductCategoryRepositoryCustom {

    List<ProductCategory> findByKeyword(String keyword, String language);

}
