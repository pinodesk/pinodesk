package pinodesk.repository;

import com.pinodesk.sequel.repository.AbstractRepository;
import com.pinodesk.sequel.sql.Where;
import lombok.RequiredArgsConstructor;
import pinodesk.entity.DoctorCategory;

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
