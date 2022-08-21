package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.domain.DoctorCategory;

public interface DoctorCategoryRepositoryCustom {

    List<DoctorCategory> findByKeyword(String keyword, String language);

}
