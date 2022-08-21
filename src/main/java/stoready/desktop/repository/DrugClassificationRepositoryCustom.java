package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.domain.DrugClassification;

public interface DrugClassificationRepositoryCustom {

    List<DrugClassification> findByKeyword(String keyword, String language);

}
