package toska.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import toska.desktop.domain.DrugCategoryBase;

@Repository
public class DrugCategoryBaseRepositoryImpl extends AbstractRepository<DrugCategoryBase> implements DrugCategoryBaseRepository {
    
}
