package pinus.desktop.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.DrugClassification;

@Repository
public interface DrugClassificationRepository
        extends PagingAndSortingRepository<DrugClassification, Long>, DrugClassificationRepositoryCustom {

    Optional<DrugClassification> findByIdAndDeletedAtIsNull(Long id);

    Optional<DrugClassification> findByLanguageAndCodeAndDeletedAtIsNull(String language, String code);

    boolean existsByCodeAndDeletedAtIsNull(String code);
}
