package pinus.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.ConfigurationConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.DrugCategoryBaseRepository;
import pinus.desktop.repository.DrugCategoryRepository;
import pinus.desktop.viewmodel.DrugCategoryBaseVM;
import pinus.desktop.viewmodel.DrugCategoryVM;

@Service
public class DrugCategoryService extends BaseService {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DrugCategoryRepository drugCategoryRepository;

    @Autowired
    private DrugCategoryBaseRepository drugCategoryBaseRepository;

    @Cacheable(CacheNameConstants.DRUG_CATEGORIES_BY_KEYWORD)
    public List<DrugCategoryVM> searchDrugCategoriesByKeyword(String keyword) {
        String drugCategoryBaseId = configurationService.getConfiguration(ConfigurationConstants.DRUG_CATEGORY_BASE_ID);
        return objectConverter.convertList(
                drugCategoryRepository.findByKeyword(keyword, Long.valueOf(drugCategoryBaseId)),
                DrugCategoryVM.class);
    }

    public DrugCategoryVM getDrugCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                drugCategoryRepository.findByIdAndDeletedAtIsNull(id),
                DrugCategoryVM.class,
                new DomainException(DomainError.DRUG_CATEGORY_NOT_FOUND_BY_ID));
    }

    @Cacheable(CacheNameConstants.DRUG_CATEGORY_BASES_ALL)
    public List<DrugCategoryBaseVM> getAllDrugCategoryBases() {
        return objectConverter
                .convertList(drugCategoryBaseRepository.findByDeletedAtIsNull(), DrugCategoryBaseVM.class);
    }

}
