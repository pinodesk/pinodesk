package tosca.desktop.service;

import java.util.List;

import tosca.desktop.constant.CacheName;
import tosca.desktop.constant.ConfigurationConstants;
import tosca.desktop.constant.DomainError;
import tosca.desktop.exception.DomainException;
import tosca.desktop.repository.DrugCategoryBaseRepository;
import tosca.desktop.repository.DrugCategoryRepository;
import tosca.desktop.viewmodel.DrugCategoryBaseVM;
import tosca.desktop.viewmodel.DrugCategoryVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DrugCategoryService extends BaseService {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DrugCategoryRepository drugCategoryRepository;

    @Autowired
    private DrugCategoryBaseRepository drugCategoryBaseRepository;

    @Cacheable(CacheName.Keys.DRUG_CATEGORIES_BY_KEYWORD)
    public List<DrugCategoryVM> searchDrugCategoriesByKeyword(String keyword) {
        String drugCategoryBaseId = configurationService.getConfiguration(ConfigurationConstants.DRUG_CATEGORY_BASE_ID);
        return objectConverter.convertList(drugCategoryRepository.filter(keyword, Long.valueOf(drugCategoryBaseId)),
                DrugCategoryVM.class);
    }

    public DrugCategoryVM getDrugCategoryById(Long id) {
        return objectConverter.convertOptionalOrThrow(drugCategoryRepository.readOne(id), DrugCategoryVM.class,
                new DomainException(DomainError.DRUG_CATEGORY_NOT_FOUND_BY_ID));
    }

    @Cacheable(CacheName.Keys.DRUG_CATEGORY_BASES_ALL)
    public List<DrugCategoryBaseVM> getAllDrugCategoryBases() {
        return objectConverter.convertList(drugCategoryBaseRepository.read(), DrugCategoryBaseVM.class);
    }

}
