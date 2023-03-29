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
import pospino.desktop.repository.UnitRepository;
import pospino.desktop.viewmodel.UnitVM;

@Service
public class UnitService extends BaseService {

    @Autowired
    private UnitRepository unitRepository;

    @ForActivity(Activity.GET_ALL_UNITS)
    @Cacheable(CacheNameConstants.UNITS_ALL)
    public List<UnitVM> getAllUnits() {
        return objectConverter.convertList(unitRepository.findByDeletedAtIsNull(), UnitVM.class);
    }

    @ForActivity(Activity.SEARCH_UNITS_BY_KEYWORD)
    @Cacheable(CacheNameConstants.UNITS_BY_KEYWORD)
    public List<UnitVM> searchUnitByKeyword(String keyword) {
        return objectConverter.convertList(unitRepository.findByKeyword(keyword, 10), UnitVM.class);
    }

    @ForActivity(Activity.GET_UNIT_BY_ID)
    public UnitVM getUnitById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                unitRepository.findByIdAndDeletedAtIsNull(id),
                UnitVM.class,
                new DomainException(DomainError.UNIT_NOT_FOUND_BY_ID));
    }

    public UnitVM getUnitByLabel(String label) {
        return objectConverter.convertOptionalOrThrow(
                unitRepository.findByLabelAndDeletedAtIsNull(label),
                UnitVM.class,
                new DomainException(DomainError.UNIT_NOT_FOUND_BY_LABEL));
    }

}
