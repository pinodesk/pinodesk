package com.getkembang.kembangdesktop.service;

import java.util.List;

import com.getkembang.kembangdesktop.constant.CacheName;
import com.getkembang.kembangdesktop.repository.LanguageRepository;
import com.getkembang.kembangdesktop.viewmodel.LanguageVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class LanguageService extends BaseService {
    
    @Autowired
    private LanguageRepository languageRepository;

    @Cacheable(CacheName.Keys.LANGUAGES_ALL)
    public List<LanguageVM> getAllLanguages() {
        return convertList(languageRepository.read(), LanguageVM.class);
    }

}
