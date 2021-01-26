package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Unit;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Limit;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class UnitRepositoryImpl extends AbstractRepository<Unit> implements UnitRepository {

    @Override
    public List<Unit> filter(String keyword, int limit) {
        return read(new Where().containsIgnoreCase(Unit.C_NAME, keyword).orContainsIgnoreCase(Unit.C_LABEL, keyword),
                new Limit(limit));
    }

}
