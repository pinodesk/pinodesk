package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.DoctorFilterVM;
import pinodesk.viewmodel.DoctorVM;

public interface DoctorRepositoryCustom {

    List<DoctorVM> findByKeyword(String keyword, String language);

    List<DoctorVM> findByFilter(DoctorFilterVM filter, String language);
}
