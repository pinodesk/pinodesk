package stoready.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import stoready.desktop.domain.ProductCategory;

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
