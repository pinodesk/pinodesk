package com.getkembang.kembangdesktop.service;

import java.util.List;

import com.getkembang.kembangdesktop.constant.CacheName;
import com.getkembang.kembangdesktop.constant.DomainError;
import com.getkembang.kembangdesktop.exception.DomainException;
import com.getkembang.kembangdesktop.repository.RackRepository;
import com.getkembang.kembangdesktop.viewmodel.RackVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RackService extends BaseService {

    @Autowired
    private RackRepository rackRepository;

    @Cacheable(CacheName.Keys.RACKS_ALL)
    public List<RackVM> getAllRacks() {
        return objectConverter.convertList(rackRepository.read(), RackVM.class);
    }

    @Cacheable(CacheName.Keys.RACKS_BY_KEYWORD)
    public List<RackVM> searchRackByKeyword(String keyword) {
        return objectConverter.convertList(rackRepository.filter(keyword, 10), RackVM.class);
    }

    public RackVM getRackById(Long id) {
        return objectConverter.convertOptionalOrThrow(rackRepository.readOne(id), RackVM.class,
                new DomainException(DomainError.RACK_NOT_FOUND_BY_ID));
    }

}
