package pinodesk.repository;

import java.util.List;

import com.pinodesk.sequel.repository.AbstractRepository;
import com.pinodesk.sequel.sql.Order;
import com.pinodesk.sequel.sql.Where;

import pinodesk.entity.ProductCategory;

public class ProductCategoryRepositoryImpl extends AbstractRepository<ProductCategory>
        implements ProductCategoryRepositoryCustom {

    @Override
    public List<ProductCategory> findByKeyword(String keyword, String language) {
        return read(
                new Where().equals(ProductCategory.C_LANGUAGE, language).and(
                        new Where().containsIgnoreCase(ProductCategory.C_NAME, keyword)
                                .orContains(ProductCategory.C_CODE, keyword)),
                new Order().by(ProductCategory.C_NAME));
    }

}
