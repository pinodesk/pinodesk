package stoready.desktop.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import stoready.desktop.domain.Payable;

@Repository
public interface PayableRepository extends PagingAndSortingRepository<Payable, Long>, PayableRepositoryCustom {

    Optional<Payable> findByPurchaseId(Long purchaseId);
}
