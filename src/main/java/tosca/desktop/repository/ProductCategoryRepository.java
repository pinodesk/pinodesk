package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.ProductCategory;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface ProductCategoryRepository extends CommonRepository<ProductCategory> {
    
    List<ProductCategory> filter(String keyword, String languageCode);

}
