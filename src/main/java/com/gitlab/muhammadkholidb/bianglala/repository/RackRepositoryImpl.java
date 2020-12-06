package com.gitlab.muhammadkholidb.bianglala.repository;

import static com.gitlab.muhammadkholidb.bianglala.constant.StringConstants.PERCENT;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.Rack;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class RackRepositoryImpl extends AbstractRepository<Rack> implements RackRepository {
    
    @Override
    public List<Rack> filter(String keyword, int limit) {
        return read(new Where()
                .likeIgnoreCase(Rack.C_NAME, StringUtils.join(PERCENT, keyword.toLowerCase(), PERCENT))
                .orLikeIgnoreCase(Rack.C_CODE, StringUtils.join(PERCENT, keyword.toLowerCase(), PERCENT)),
                limitFactory.create(limit));
    }

}
