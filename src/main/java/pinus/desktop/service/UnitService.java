package pinus.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.UnitRepository;
import pinus.desktop.viewmodel.UnitVM;

@Service
public class UnitService extends BaseService {

    @Autowired
    private UnitRepository unitRepository;

    @Cacheable(CacheNameConstants.UNITS_ALL)
    public List<UnitVM> getAllUnits() {
        return objectConverter.convertList(unitRepository.read(), UnitVM.class);
    }

    @Cacheable(CacheNameConstants.UNITS_BY_KEYWORD)
    public List<UnitVM> searchUnitByKeyword(String keyword) {
        return objectConverter.convertList(unitRepository.filter(keyword, 10), UnitVM.class);
    }

    public UnitVM getUnitById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                unitRepository.readOne(id),
                UnitVM.class,
                new DomainException(DomainError.UNIT_NOT_FOUND_BY_ID));
    }

}
