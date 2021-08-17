package tosca.desktop.repository;

import java.util.List;

import tosca.desktop.domain.Unit;
import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Limit;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

@Repository
public class UnitRepositoryImpl extends AbstractRepository<Unit> implements UnitRepository {

    @Override
    public List<Unit> filter(String keyword, int limit) {
        return read(new Where().containsIgnoreCase(Unit.C_NAME, keyword).orContainsIgnoreCase(Unit.C_LABEL, keyword),
                new Limit(limit));
    }

}
