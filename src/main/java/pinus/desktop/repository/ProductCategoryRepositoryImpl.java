package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.domain.ProductCategory;

public class ProductCategoryRepositoryImpl extends AbstractRepository<ProductCategory>
        implements ProductCategoryRepositoryCustom {

    @Override
    public List<ProductCategory> findByKeyword(String keyword, String languageCode) {
        return read(
                new Where().equals(ProductCategory.C_LANGUAGE_CODE, languageCode).and(
                        new Where().containsIgnoreCase(ProductCategory.C_NAME, keyword)
                                .orContains(ProductCategory.C_CODE, keyword)),
                new Order().by(ProductCategory.C_NAME));
    }

}
