package pinus.desktop.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.DrugCategory;

@Repository
public interface DrugCategoryRepository
        extends PagingAndSortingRepository<DrugCategory, Long>, DrugCategoryRepositoryCustom {

    Optional<DrugCategory> findByIdAndDeletedAtIsNull(Long id);

}
