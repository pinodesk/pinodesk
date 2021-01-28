package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Rack;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface RackRepository extends CommonRepository<Rack> {
    
    List<Rack> filter(String keyword, int limit);

}
