package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.Unit;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.CommonRepository;

public interface UnitRepository extends CommonRepository<Unit> {
    
    List<Unit> filter(String keyword, int limit);

}
