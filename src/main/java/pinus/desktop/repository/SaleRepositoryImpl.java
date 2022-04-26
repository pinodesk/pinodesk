package pinus.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Sale;

@Repository
public class SaleRepositoryImpl extends AbstractRepository<Sale> implements SaleRepository {

}
