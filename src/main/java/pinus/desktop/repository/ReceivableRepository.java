package pinus.desktop.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Receivable;

@Repository
public interface ReceivableRepository extends PagingAndSortingRepository<Receivable, Long>, ReceivableRepositoryCustom {

}
