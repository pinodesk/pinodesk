package com.pinodesk.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.pinodesk.annotation.TargetActivity;
import com.pinodesk.constant.Activity;
import com.pinodesk.constant.CacheNameConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.DrugClassificationRepository;
import com.pinodesk.viewmodel.DrugClassificationVM;

@Service
public class DrugClassificationService extends BaseService {

    @Autowired
    private DrugClassificationRepository drugClassificationRepository;

    @TargetActivity(Activity.SEARCH_DRUG_CLASSIFICATIONS_BY_KEYWORD)
    @Cacheable(CacheNameConstants.DRUG_CLASSIFICATION_BY_KEYWORD)
    public List<DrugClassificationVM> searchDrugClassificationsByKeyword(String keyword, String language) {
        return objectConverter
                .convertList(drugClassificationRepository.findByKeyword(keyword, language), DrugClassificationVM.class);
    }

    @TargetActivity(Activity.GET_DRUG_CLASSIFICATION_BY_ID)
    public DrugClassificationVM getDrugClassificationById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                drugClassificationRepository.findByIdAndDeletedAtIsNull(id),
                DrugClassificationVM.class,
                new DomainException(DomainError.DRUG_CLASSIFICATION_NOT_FOUND_BY_ID));
    }

    @TargetActivity(Activity.GET_DRUG_CLASSIFICATION_BY_CODE)
    public DrugClassificationVM getDrugClassificationByCode(String classificationCode, String language) {
        return objectConverter.convertOptionalOrThrow(
                drugClassificationRepository.findByLanguageAndCodeAndDeletedAtIsNull(language, classificationCode),
                DrugClassificationVM.class,
                new DomainException(DomainError.DRUG_CLASSIFICATION_NOT_FOUND_BY_CODE));
    }

}
