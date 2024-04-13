package pinodesk.repository;

import java.util.List;

import pinodesk.domain.DrugClassification;

public interface DrugClassificationRepositoryCustom {

    List<DrugClassification> findByKeyword(String keyword, String language);

}
