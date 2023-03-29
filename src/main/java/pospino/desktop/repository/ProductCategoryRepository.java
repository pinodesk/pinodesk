package pospino.desktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pospino.desktop.domain.ProductCategory;

@Repository
public interface ProductCategoryRepository
        extends PagingAndSortingRepository<ProductCategory, Long>, ProductCategoryRepositoryCustom {

    Optional<ProductCategory> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByCodeAndDeletedAtIsNull(String code);

    List<ProductCategory> findByLanguageAndDeletedAtIsNullOrderByName(String language);

    Optional<ProductCategory> findByCodeAndLanguageAndDeletedAtIsNull(String code, String language);

}
