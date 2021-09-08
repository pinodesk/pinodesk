package toscabox.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import toscabox.desktop.domain.Drug;

@Repository
public class DrugRepositoryImpl extends AbstractRepository<Drug> implements DrugRepository {

}
