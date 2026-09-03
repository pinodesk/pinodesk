package com.pinodesk.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.pinodesk.entity.DrugClassification;

@Repository
public interface DrugClassificationRepository
        extends PagingAndSortingRepository<DrugClassification, Long>, DrugClassificationRepositoryCustom {

    Optional<DrugClassification> findByIdAndDeletedAtIsNull(Long id);

    Optional<DrugClassification> findByLanguageAndCodeAndDeletedAtIsNull(String language, String code);

    boolean existsByCodeAndDeletedAtIsNull(String code);
}
