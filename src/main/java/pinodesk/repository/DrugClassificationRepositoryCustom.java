package pinodesk.repository;

import java.util.List;

import pinodesk.entity.DrugClassification;

public interface DrugClassificationRepositoryCustom {

    List<DrugClassification> findByKeyword(String keyword, String language);

}
