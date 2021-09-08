package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.ProductCategory;

public interface ProductCategoryRepository extends CommonRepository<ProductCategory> {

    List<ProductCategory> filter(String keyword, String languageCode);

}
