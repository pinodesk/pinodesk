package tosca.desktop.repository;

import tosca.desktop.domain.Drug;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

@Repository
public class DrugRepositoryImpl extends AbstractRepository<Drug> implements DrugRepository {
    
}
