package stoready.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import stoready.desktop.annotation.ForActivity;
import stoready.desktop.constant.Activity;
import stoready.desktop.constant.CacheNameConstants;
import stoready.desktop.constant.DomainError;
import stoready.desktop.exception.DomainException;
import stoready.desktop.repository.UnitRepository;
import stoready.desktop.viewmodel.UnitVM;

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

}
