package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.domain.Unit;

public interface UnitRepositoryCustom {

    List<Unit> findByKeyword(String keyword, String language);

}
