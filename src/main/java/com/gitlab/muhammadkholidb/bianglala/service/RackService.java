package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.constant.DomainError;
import com.gitlab.muhammadkholidb.bianglala.exception.DomainException;
import com.gitlab.muhammadkholidb.bianglala.repository.RackRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.RackVM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RackService extends BaseService {

    @Autowired
    private RackRepository rackRepository;

    @Cacheable("racksAll")
    public List<RackVM> getAllRacks() {
        return convertList(rackRepository.read(), RackVM.class);
    }

    @Cacheable("racksByKeyword")
    public List<RackVM> searchRackByKeyword(String keyword) {
        return convertList(rackRepository.filter(keyword, 10), RackVM.class);
    }

    public RackVM getRackById(Long id) {
        return convertOptionalOrThrow(rackRepository.readOne(id), RackVM.class,
                new DomainException(DomainError.RACK_NOT_FOUND_BY_ID));
    }

}
