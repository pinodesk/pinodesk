package pinodesk.repository;

import java.util.List;

import com.mudiatech.sequel.repository.AbstractRepository;
import com.mudiatech.sequel.sql.Order;
import com.mudiatech.sequel.sql.Where;

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
