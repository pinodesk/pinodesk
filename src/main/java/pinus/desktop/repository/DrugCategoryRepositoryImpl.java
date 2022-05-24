package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.domain.DrugCategory;

public class DrugCategoryRepositoryImpl extends AbstractRepository<DrugCategory>
        implements DrugCategoryRepositoryCustom {

    @Override
    public List<DrugCategory> findByKeyword(String keyword, Long drugCategoryBaseId) {
        return read(
                new Where().equals(DrugCategory.C_CATEGORY_BASE_ID, drugCategoryBaseId)
                        .andContainsIgnoreCase(DrugCategory.C_NAME, keyword));
    }

}
