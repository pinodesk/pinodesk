package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Limit;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.domain.ProductCategory;

public class ProductCategoryRepositoryImpl extends AbstractRepository<ProductCategory>
        implements ProductCategoryRepositoryCustom {

    @Override
    public List<ProductCategory> findByKeyword(String keyword, String languageCode) {
        return read(
                new Where().equals(ProductCategory.C_LANGUAGE_CODE, languageCode)
                        .andContainsIgnoreCase(ProductCategory.C_NAME, keyword),
                new Limit(10));
    }

}
