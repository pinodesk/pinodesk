package com.gitlab.muhammadkholidb.bianglala.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.Rack;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class RackRepositoryImpl extends AbstractRepository<Rack> implements RackRepository {

    @Override
    public List<Rack> filter(String keyword, int limit) {
        return read(new Where().containsIgnoreCase(Rack.C_NAME, keyword).orContainsIgnoreCase(Rack.C_CODE, keyword),
                limitFactory.create(limit));
    }

}
