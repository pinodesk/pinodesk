package pinus.desktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.ProductExpiry;

@Repository
public interface ProductExpiryRepository extends PagingAndSortingRepository<ProductExpiry, Long> {

    List<ProductExpiry> findByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);

    Optional<ProductExpiry> findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);

    Optional<ProductExpiry> findFirstByProductIdAndDeletedAtIsNullOrderByExpiredDate(Long productId);
}
