package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Unit;
import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

public interface UnitRepository extends CommonRepository<Unit> {
    
    List<Unit> filter(String keyword, int limit);

}
