package com.gitlab.muhammadkholidb.bianglala.repository;

import static com.gitlab.muhammadkholidb.bianglala.constant.StringConstants.PERCENT;

import java.util.List;

import com.gitlab.muhammadkholidb.bianglala.domain.Unit;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.jdbctemplatehelper.sql.Where;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class UnitRepositoryImpl extends AbstractRepository<Unit> implements UnitRepository {

    @Override
    public List<Unit> filter(String keyword, int limit) {
        return read(new Where()
                .likeIgnoreCase(Unit.C_NAME, StringUtils.join(PERCENT, keyword.toLowerCase(), PERCENT))
                .orLikeIgnoreCase(Unit.C_LABEL, StringUtils.join(PERCENT, keyword.toLowerCase(), PERCENT)),
                limitFactory.create(limit));
    }

}
