package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.entity.Unit;

public interface UnitRepositoryCustom {

    List<Unit> findByKeyword(String keyword, String language);

}
