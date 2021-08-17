package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.DrugCategory;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Limit;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class DrugCategoryRepositoryImpl extends AbstractRepository<DrugCategory> implements DrugCategoryRepository {

    @Override
    public List<DrugCategory> filter(String keyword, Long drugCategoryBaseId) {
        return read(new Where().equals(DrugCategory.C_CATEGORY_BASE_ID, drugCategoryBaseId)
                .andContainsIgnoreCase(DrugCategory.C_NAME, keyword), new Limit(10));
    }

}
