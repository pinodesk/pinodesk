package pinodesk.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pinodesk.annotation.TargetActivity;
import pinodesk.constant.Activity;
import pinodesk.constant.CacheNameConstants;
import pinodesk.constant.ConfigurationConstants;
import pinodesk.constant.DomainError;
import pinodesk.entity.Unit;
import pinodesk.exception.DomainException;
import pinodesk.repository.UnitRepository;
import pinodesk.viewmodel.UnitVM;

@Service
public class UnitService extends BaseService {

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private ConfigurationService configurationService;

    @TargetActivity(Activity.GET_ALL_UNITS)
    @Cacheable(CacheNameConstants.UNITS_ALL)
    public List<UnitVM> getAllUnits() {
        return objectConverter.convertList(unitRepository.findByDeletedAtIsNull(), UnitVM.class);
    }

    @TargetActivity(Activity.SEARCH_UNITS_BY_KEYWORD)
    @Cacheable(CacheNameConstants.UNITS_BY_KEYWORD)
    public List<UnitVM> searchUnitByKeyword(String keyword) {
        String language = configurationService.getConfiguration(ConfigurationConstants.LANGUAGE);
        List<Unit> units;
        if (StringUtils.isBlank(keyword)) {
            units = unitRepository.findByLanguageAndDeletedAtIsNullOrderByName(language);
        } else {
            units = unitRepository.findByKeyword(keyword, language);
        }
        return objectConverter.convertList(units, UnitVM.class);
    }

    @TargetActivity(Activity.GET_UNIT_BY_ID)
    public UnitVM getUnitById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                unitRepository.findByIdAndDeletedAtIsNull(id),
                UnitVM.class,
                new DomainException(DomainError.UNIT_NOT_FOUND_BY_ID));
    }

    public UnitVM getUnitByCode(String code, String language) {
        return objectConverter.convertOptionalOrThrow(
                unitRepository.findByLanguageAndCodeAndDeletedAtIsNull(language, code),
                UnitVM.class,
                new DomainException(DomainError.UNIT_NOT_FOUND_BY_CODE));
    }

}
