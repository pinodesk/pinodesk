package pinus.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Drug;

@Repository
public class DrugRepositoryImpl extends AbstractRepository<Drug> implements DrugRepository {

}
