package com.getkembang.kembangdesktop.repository;

import java.util.List;

import com.getkembang.kembangdesktop.domain.Rack;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Limit;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class RackRepositoryImpl extends AbstractRepository<Rack> implements RackRepository {

    @Override
    public List<Rack> filter(String keyword, int limit) {
        return read(new Where().containsIgnoreCase(Rack.C_NAME, keyword).orContainsIgnoreCase(Rack.C_CODE, keyword),
                new Limit(limit));
    }

}
