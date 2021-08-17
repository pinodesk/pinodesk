package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.DrugCategory;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface DrugCategoryRepository extends CommonRepository<DrugCategory> {
    
    List<DrugCategory> filter(String keyword, Long drugCategoryBaseId);

}
