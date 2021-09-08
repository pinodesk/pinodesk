package toscabox.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import toscabox.desktop.domain.DrugCategoryBase;

@Repository
public class DrugCategoryBaseRepositoryImpl extends AbstractRepository<DrugCategoryBase>
        implements DrugCategoryBaseRepository {

}
