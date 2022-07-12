package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.DrugClassification;

public interface DrugClassificationRepositoryCustom {

    List<DrugClassification> findByKeyword(String keyword, String language);

}
