package pinodesk.repository;

import java.util.List;

import com.pinodesk.sequel.repository.AbstractRepository;
import com.pinodesk.sequel.sql.Order;
import com.pinodesk.sequel.sql.Where;

import pinodesk.entity.DrugClassification;

public class DrugClassificationRepositoryImpl extends AbstractRepository<DrugClassification>
        implements DrugClassificationRepositoryCustom {

    @Override
    public List<DrugClassification> findByKeyword(String keyword, String language) {
        return read(
                new Where().equals(DrugClassification.C_LANGUAGE, language).and(
                        new Where().containsIgnoreCase(DrugClassification.C_NAME, keyword)
                                .orContains(DrugClassification.C_CODE, keyword)),
                new Order().by(DrugClassification.C_NAME));
    }

}
