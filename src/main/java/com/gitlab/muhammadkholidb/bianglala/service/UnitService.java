package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gitlab.muhammadkholidb.bianglala.constant.DomainError;
import com.gitlab.muhammadkholidb.bianglala.exception.DomainException;
import com.gitlab.muhammadkholidb.bianglala.repository.UnitRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitSearchResult;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UnitService extends BaseService {

    @Autowired
    private UnitRepository unitRepository;

    @Cacheable("getAllUnits")
    public List<UnitSearchResult> getAllUnits() {
        return unitRepository.read().stream().map(unit -> {
            UnitSearchResult result = new UnitSearchResult();
            BeanUtils.copyProperties(unit, result);
            return result;
        }).collect(Collectors.toList());
    }

    @Cacheable("searchUnitByKeyword")
    public List<UnitSearchResult> searchUnitByKeyword(String keyword) {
        return unitRepository.filter(keyword, 10).stream().map(unit -> {
            UnitSearchResult result = new UnitSearchResult();
            BeanUtils.copyProperties(unit, result);
            return result;
        }).collect(Collectors.toList());
    }

    public UnitSearchResult getUnitById(Long id) {
        return unitRepository.readOne(id)
                .map(pc -> objectMapper.convertValue(pc, UnitSearchResult.class))
                .orElseThrow(() -> new DomainException(DomainError.NOT_FOUND));
    }

}
