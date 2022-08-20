package stoready.desktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import stoready.desktop.domain.ProductStock;

@Repository
public interface ProductStockRepository extends PagingAndSortingRepository<ProductStock, Long> {

    List<ProductStock> findByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);

    Optional<ProductStock> findFirstByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);
}
