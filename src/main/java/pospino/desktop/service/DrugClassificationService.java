package pospino.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pospino.desktop.annotation.ForActivity;
import pospino.desktop.constant.Activity;
import pospino.desktop.constant.CacheNameConstants;
import pospino.desktop.constant.DomainError;
import pospino.desktop.exception.DomainException;
import pospino.desktop.repository.DrugClassificationRepository;
import pospino.desktop.viewmodel.DrugClassificationVM;

@Service
public class DrugClassificationService extends BaseService {

    @Autowired
    private DrugClassificationRepository drugClassificationRepository;

    @ForActivity(Activity.SEARCH_DRUG_CLASSIFICATIONS_BY_KEYWORD)
    @Cacheable(CacheNameConstants.DRUG_CLASSIFICATION_BY_KEYWORD)
    public List<DrugClassificationVM> searchDrugClassificationsByKeyword(String keyword, String language) {
        return objectConverter
                .convertList(drugClassificationRepository.findByKeyword(keyword, language), DrugClassificationVM.class);
    }

    @ForActivity(Activity.GET_DRUG_CLASSIFICATION_BY_ID)
    public DrugClassificationVM getDrugClassificationById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                drugClassificationRepository.findByIdAndDeletedAtIsNull(id),
                DrugClassificationVM.class,
                new DomainException(DomainError.DRUG_CLASSIFICATION_NOT_FOUND_BY_ID));
    }

    @ForActivity(Activity.GET_DRUG_CLASSIFICATION_BY_CODE)
    public DrugClassificationVM getDrugClassificationByCode(String classificationCode, String language) {
        return objectConverter.convertOptionalOrThrow(
                drugClassificationRepository.findByLanguageAndCodeAndDeletedAtIsNull(language, classificationCode),
                DrugClassificationVM.class,
                new DomainException(DomainError.DRUG_CLASSIFICATION_NOT_FOUND_BY_CODE));
    }

}
