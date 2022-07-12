package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Order;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import pinus.desktop.domain.DrugClassification;

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
