package pinodesk.repository;

import java.util.List;

import pinodesk.domain.DoctorCategory;

public interface DoctorCategoryRepositoryCustom {

    List<DoctorCategory> findByKeyword(String keyword, String language);

}
