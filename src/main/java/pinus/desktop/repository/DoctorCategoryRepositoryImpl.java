package pinus.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;
import lombok.RequiredArgsConstructor;
import pinus.desktop.domain.DoctorCategory;

import java.util.List;

@RequiredArgsConstructor
public class DoctorCategoryRepositoryImpl extends AbstractRepository<DoctorCategory>
        implements DoctorCategoryRepositoryCustom {

    @Override
    public List<DoctorCategory> findByKeyword(String keyword, String languageCode) {
        return read(
                new Where().equals(DoctorCategory.C_LANGUAGE_CODE, languageCode).and(
                        new Where().containsIgnoreCase(DoctorCategory.C_NAME, keyword)
                                .orContains(DoctorCategory.C_CODE, keyword)));
    }

}
