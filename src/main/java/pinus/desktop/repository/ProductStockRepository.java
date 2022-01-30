package pinus.desktop.repository;

import java.util.List;
import java.util.Optional;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.ProductStock;

public interface ProductStockRepository extends CommonRepository<ProductStock> {

    List<ProductStock> findByProductId(Long productId);

    Optional<ProductStock> findTopByProductId(Long productId);
}
