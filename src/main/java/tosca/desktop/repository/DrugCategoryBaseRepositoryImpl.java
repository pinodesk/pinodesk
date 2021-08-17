package tosca.desktop.repository;

import tosca.desktop.domain.DrugCategoryBase;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

@Repository
public class DrugCategoryBaseRepositoryImpl extends AbstractRepository<DrugCategoryBase> implements DrugCategoryBaseRepository {
    
}
