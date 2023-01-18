package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.domain.DrugClassification;

public interface DrugClassificationRepositoryCustom {

    List<DrugClassification> findByKeyword(String keyword, String language);

}
