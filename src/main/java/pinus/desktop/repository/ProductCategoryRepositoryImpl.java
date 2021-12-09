package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Limit;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.ProductCategory;

@Repository
public class ProductCategoryRepositoryImpl extends AbstractRepository<ProductCategory>
        implements ProductCategoryRepository {

    @Override
    public List<ProductCategory> filter(String keyword, String languageCode) {
        return read(
                new Where().equals(ProductCategory.C_LANGUAGE_CODE, languageCode)
                        .andContainsIgnoreCase(ProductCategory.C_NAME, keyword),
                new Limit(10));
    }

}
