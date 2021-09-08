package toscabox.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.CommonRepository;

import toscabox.desktop.domain.Unit;

public interface UnitRepository extends CommonRepository<Unit> {

    List<Unit> filter(String keyword, int limit);

}
