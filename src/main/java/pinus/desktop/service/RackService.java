package pinus.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.RackRepository;
import pinus.desktop.viewmodel.RackVM;

@Service
public class RackService extends BaseService {

    @Autowired
    private RackRepository rackRepository;

    @Cacheable(CacheNameConstants.RACKS_ALL)
    public List<RackVM> getAllRacks() {
        return objectConverter.convertList(rackRepository.read(), RackVM.class);
    }

    @Cacheable(CacheNameConstants.RACKS_BY_KEYWORD)
    public List<RackVM> searchRackByKeyword(String keyword) {
        return objectConverter.convertList(rackRepository.filter(keyword, 10), RackVM.class);
    }

    public RackVM getRackById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                rackRepository.readOne(id),
                RackVM.class,
                new DomainException(DomainError.RACK_NOT_FOUND_BY_ID));
    }

}
