package toska.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import toska.desktop.domain.Drug;

@Repository
public class DrugRepositoryImpl extends AbstractRepository<Drug> implements DrugRepository {
    
}
