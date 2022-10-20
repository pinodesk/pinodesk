package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.DoctorFilterVM;
import stoready.desktop.viewmodel.DoctorVM;

public interface DoctorRepositoryCustom {

    List<DoctorVM> findByKeyword(String keyword, String language);

    List<DoctorVM> findByFilter(DoctorFilterVM filter, String language);
}
