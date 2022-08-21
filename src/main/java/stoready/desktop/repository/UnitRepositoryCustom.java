package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.domain.Unit;

public interface UnitRepositoryCustom {

    List<Unit> findByKeyword(String keyword, int limit);

}
