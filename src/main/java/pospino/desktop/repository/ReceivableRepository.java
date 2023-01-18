package pospino.desktop.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pospino.desktop.domain.Receivable;

@Repository
public interface ReceivableRepository extends PagingAndSortingRepository<Receivable, Long>, ReceivableRepositoryCustom {

    Optional<Receivable> findBySaleId(Long saleId);
}
