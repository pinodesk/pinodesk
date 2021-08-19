package toska.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import toska.desktop.constant.CacheName;
import toska.desktop.constant.DomainError;
import toska.desktop.exception.DomainException;
import toska.desktop.repository.UnitRepository;
import toska.desktop.viewmodel.UnitVM;

@Service
public class UnitService extends BaseService {

    @Autowired
    private UnitRepository unitRepository;

    @Cacheable(CacheName.Keys.UNITS_ALL)
    public List<UnitVM> getAllUnits() {
        return objectConverter.convertList(unitRepository.read(), UnitVM.class);
    }

    @Cacheable(CacheName.Keys.UNITS_BY_KEYWORD)
    public List<UnitVM> searchUnitByKeyword(String keyword) {
        return objectConverter.convertList(unitRepository.filter(keyword, 10), UnitVM.class);
    }

    public UnitVM getUnitById(Long id) {
        return objectConverter.convertOptionalOrThrow(unitRepository.readOne(id), UnitVM.class,
                new DomainException(DomainError.UNIT_NOT_FOUND_BY_ID));
    }

}
