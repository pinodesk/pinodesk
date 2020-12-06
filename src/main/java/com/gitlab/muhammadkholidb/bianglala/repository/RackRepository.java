package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.Rack;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.CommonRepository;

public interface RackRepository extends CommonRepository<Rack> {
    
    List<Rack> filter(String keyword, int limit);

}
