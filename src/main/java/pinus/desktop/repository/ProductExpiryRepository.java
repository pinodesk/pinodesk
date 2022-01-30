package pinus.desktop.repository;

import java.util.List;
import java.util.Optional;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.ProductExpiry;

public interface ProductExpiryRepository extends CommonRepository<ProductExpiry> {

    List<ProductExpiry> findByProductId(Long productId);

    Optional<ProductExpiry> findTopByProductId(Long productId);

    Optional<ProductExpiry> findTopByProductIdOrderByExpiredDate(Long productId);
}
