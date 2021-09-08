package toscabox.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import toscabox.desktop.constant.CacheNameConstants;
import toscabox.desktop.constant.ConfigurationConstants;
import toscabox.desktop.constant.DomainError;
import toscabox.desktop.exception.DomainException;
import toscabox.desktop.repository.DrugCategoryBaseRepository;
import toscabox.desktop.repository.DrugCategoryRepository;
import toscabox.desktop.viewmodel.DrugCategoryBaseVM;
import toscabox.desktop.viewmodel.DrugCategoryVM;

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
                drugCategoryRepository.filter(keyword, Long.valueOf(drugCategoryBaseId)),
                DrugCategoryVM.class);
    }

    public DrugCategoryVM getDrugCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                drugCategoryRepository.readOne(id),
                DrugCategoryVM.class,
                new DomainException(DomainError.DRUG_CATEGORY_NOT_FOUND_BY_ID));
    }

    @Cacheable(CacheNameConstants.DRUG_CATEGORY_BASES_ALL)
    public List<DrugCategoryBaseVM> getAllDrugCategoryBases() {
        return objectConverter.convertList(drugCategoryBaseRepository.read(), DrugCategoryBaseVM.class);
    }

}
