package com.getkembang.kembangdesktop.service;

import java.util.List;

import com.getkembang.kembangdesktop.constant.ConfigurationConstants;
import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.DrugCategoryRepository;
import com.getkembang.kembangdesktop.viewmodel.DrugCategoryVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DrugCategoryService extends BaseService {

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private DrugCategoryRepository drugCategoryRepository;

    @Cacheable("drugCategoriesByKeyword")
    public List<DrugCategoryVM> searchDrugCategoriesByKeyword(String keyword) {
        String drugCategoryBaseId = configurationService.getConfiguration(ConfigurationConstants.DRUG_CATEGORY_BASE_ID);
        return convertList(drugCategoryRepository.filter(keyword, Long.valueOf(drugCategoryBaseId)), DrugCategoryVM.class);
    }

    public DrugCategoryVM getDrugCategoryById(Long id) {
        return convertOptionalOrThrow(drugCategoryRepository.readOne(id), DrugCategoryVM.class,
                new DomainException(DomainError.DRUG_CATEGORY_NOT_FOUND_BY_ID));
    }

}
