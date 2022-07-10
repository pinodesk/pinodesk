package pinus.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.DrugClassificationRepository;
import pinus.desktop.viewmodel.DrugClassificationVM;

@Service
public class DrugClassificationService extends BaseService {

    @Autowired
    private DrugClassificationRepository drugClassificationRepository;

    @Cacheable(CacheNameConstants.DRUG_CLASSIFICATION_BY_KEYWORD)
    public List<DrugClassificationVM> searchDrugClassificationsByKeyword(String keyword, String languageCode) {
        return objectConverter.convertList(
                drugClassificationRepository.findByKeyword(keyword, languageCode),
                DrugClassificationVM.class);
    }

    public DrugClassificationVM getDrugClassificationById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                drugClassificationRepository.findByIdAndDeletedAtIsNull(id),
                DrugClassificationVM.class,
                new DomainException(DomainError.DRUG_CLASSIFICATION_NOT_FOUND_BY_ID));
    }

    public DrugClassificationVM getDrugClassificationByCode(String classificationCode, String languageCode) {
        return objectConverter.convertOptionalOrThrow(
                drugClassificationRepository
                        .findByLanguageCodeAndCodeAndDeletedAtIsNull(languageCode, classificationCode),
                DrugClassificationVM.class,
                new DomainException(DomainError.DRUG_CLASSIFICATION_NOT_FOUND_BY_CODE));
    }

}
