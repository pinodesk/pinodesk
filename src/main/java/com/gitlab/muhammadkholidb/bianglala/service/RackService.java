package com.gitlab.muhammadkholidb.bianglala.service;

import java.util.List;
import java.util.stream.Collectors;

import com.gitlab.muhammadkholidb.bianglala.constant.DomainError;
import com.gitlab.muhammadkholidb.bianglala.exception.DomainException;
import com.gitlab.muhammadkholidb.bianglala.repository.RackRepository;
import com.gitlab.muhammadkholidb.bianglala.viewmodel.RackSearchResult;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RackService extends BaseService {

    @Autowired
    private RackRepository rackRepository;

    @Cacheable("getAllRacks")
    public List<RackSearchResult> getAllRacks() {
        return rackRepository.read().stream().map(rack -> {
            RackSearchResult result = new RackSearchResult();
            BeanUtils.copyProperties(rack, result);
            return result;
        }).collect(Collectors.toList());
    }

    @Cacheable("searchRackByKeyword")
    public List<RackSearchResult> searchRackByKeyword(String keyword) {
        return rackRepository.filter(keyword, 10).stream().map(rack -> {
            RackSearchResult result = new RackSearchResult();
            BeanUtils.copyProperties(rack, result);
            return result;
        }).collect(Collectors.toList());
    }

    public RackSearchResult getRackById(Long id) {
        return rackRepository.readOne(id)
                .map(pc -> objectMapper.convertValue(pc, RackSearchResult.class))
                .orElseThrow(() -> new DomainException(DomainError.NOT_FOUND));
    }

}
