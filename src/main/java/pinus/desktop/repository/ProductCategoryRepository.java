package pinus.desktop.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.ProductCategory;

@Repository
public interface ProductCategoryRepository
        extends PagingAndSortingRepository<ProductCategory, Long>, ProductCategoryRepositoryCustom {

    Optional<ProductCategory> findByIdAndDeletedAtIsNull(Long id);

}
