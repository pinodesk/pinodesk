package pinodesk.repository;

import java.util.List;

import pinodesk.entity.ProductCategory;

public interface ProductCategoryRepositoryCustom {

    List<ProductCategory> findByKeyword(String keyword, String language);

}
