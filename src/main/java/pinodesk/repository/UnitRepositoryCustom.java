package pinodesk.repository;

import java.util.List;

import pinodesk.domain.Unit;

public interface UnitRepositoryCustom {

    List<Unit> findByKeyword(String keyword, String language);

}
