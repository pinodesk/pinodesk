package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.constant.DomainError;
import com.gitlab.muhammadkholidb.bianglala.exception.DomainException;
import com.gitlab.muhammadkholidb.bianglala.repository.UnitRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.UnitVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class UnitService extends BaseService {

    @Autowired
    private UnitRepository unitRepository;

    @Cacheable("unitsAll")
    public List<UnitVM> getAllUnits() {
        return convertList(unitRepository.read(), UnitVM.class);
    }

    @Cacheable("unitsByKeyword")
    public List<UnitVM> searchUnitByKeyword(String keyword) {
        return convertList(unitRepository.filter(keyword, 10), UnitVM.class);
    }

    public UnitVM getUnitById(Long id) {
        return convertOptionalOrThrow(unitRepository.readOne(id), UnitVM.class,
                new DomainException(DomainError.NOT_FOUND));
    }

}
