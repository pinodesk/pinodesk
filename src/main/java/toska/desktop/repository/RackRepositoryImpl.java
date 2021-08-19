package toska.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Limit;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

import toska.desktop.domain.Rack;

@Repository
public class RackRepositoryImpl extends AbstractRepository<Rack> implements RackRepository {

    @Override
    public List<Rack> filter(String keyword, int limit) {
        return read(new Where().containsIgnoreCase(Rack.C_NAME, keyword).orContainsIgnoreCase(Rack.C_CODE, keyword),
                new Limit(limit));
    }

}
