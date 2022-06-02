package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.Unit;

public interface UnitRepositoryCustom {

    List<Unit> findByKeyword(String keyword, int limit);

}
