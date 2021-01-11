package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.constant.ConfigurationConstants;
import com.gitlab.muhammadkholidb.bianglala.constant.DomainError;
import com.gitlab.muhammadkholidb.bianglala.exception.DomainException;
import com.gitlab.muhammadkholidb.bianglala.repository.DrugCategoryRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.DrugCategoryVM;

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
                new DomainException(DomainError.NOT_FOUND));
    }

}
