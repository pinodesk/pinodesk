package pospino.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;
import lombok.RequiredArgsConstructor;
import pospino.desktop.domain.DoctorCategory;

import java.util.List;

@RequiredArgsConstructor
public class DoctorCategoryRepositoryImpl extends AbstractRepository<DoctorCategory>
        implements DoctorCategoryRepositoryCustom {

    @Override
    public List<DoctorCategory> findByKeyword(String keyword, String language) {
        return read(
                new Where().equals(DoctorCategory.C_LANGUAGE, language).and(
                        new Where().containsIgnoreCase(DoctorCategory.C_NAME, keyword)
                                .orContains(DoctorCategory.C_CODE, keyword)));
    }

}
