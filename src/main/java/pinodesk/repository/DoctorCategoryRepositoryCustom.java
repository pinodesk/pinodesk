package pinodesk.repository;

import java.util.List;

import pinodesk.entity.DoctorCategory;

public interface DoctorCategoryRepositoryCustom {

    List<DoctorCategory> findByKeyword(String keyword, String language);

}
