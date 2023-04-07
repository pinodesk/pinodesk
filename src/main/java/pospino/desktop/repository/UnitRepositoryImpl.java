package pospino.desktop.repository;

import java.util.List;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;
import com.gitlab.mudiasoft.sequel.sql.Where;

import pospino.desktop.domain.Unit;

public class UnitRepositoryImpl extends AbstractRepository<Unit> implements UnitRepositoryCustom {

    @Override
    public List<Unit> findByKeyword(String keyword, int limit) {
        return read(new Where().containsIgnoreCase(Unit.C_NAME, keyword).orContainsIgnoreCase(Unit.C_LABEL, keyword));
    }

}
