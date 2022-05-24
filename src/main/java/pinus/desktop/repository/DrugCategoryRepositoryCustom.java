package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.DrugCategory;

public interface DrugCategoryRepositoryCustom {

    List<DrugCategory> findByKeyword(String keyword, Long drugCategoryBaseId);

}
