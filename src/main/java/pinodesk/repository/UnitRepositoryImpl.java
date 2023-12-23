package pinodesk.repository;

import java.util.List;

import com.gitlab.mudiasoft.sequel.repository.AbstractRepository;
import com.gitlab.mudiasoft.sequel.sql.Order;
import com.gitlab.mudiasoft.sequel.sql.Where;

import pinodesk.domain.Unit;

public class UnitRepositoryImpl extends AbstractRepository<Unit> implements UnitRepositoryCustom {

    @Override
    public List<Unit> findByKeyword(String keyword, String language) {
        return read(
                new Where().equals(Unit.C_LANGUAGE, language).and(
                        new Where().containsIgnoreCase(Unit.C_NAME, keyword).orContains(Unit.C_CODE, keyword)
                                .orContainsIgnoreCase(Unit.C_LABEL, keyword)),
                new Order().by(Unit.C_NAME));
    }

}
