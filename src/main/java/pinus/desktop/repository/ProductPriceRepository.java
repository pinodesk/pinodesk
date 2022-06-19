package pinus.desktop.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.ProductPrice;

@Repository
public interface ProductPriceRepository extends PagingAndSortingRepository<ProductPrice, Long> {

    List<ProductPrice> findByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);

    List<ProductPrice> findFirst2ByProductIdAndDeletedAtIsNullOrderByIdDesc(Long productId);
}
