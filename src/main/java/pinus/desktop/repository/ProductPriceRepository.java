package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import pinus.desktop.domain.ProductPrice;

public interface ProductPriceRepository extends CommonRepository<ProductPrice> {

    List<ProductPrice> findByProductId(Long productId);

}
