package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.DoctorFilterVM;
import pospino.desktop.viewmodel.DoctorVM;

public interface DoctorRepositoryCustom {

    List<DoctorVM> findByKeyword(String keyword, String language);

    List<DoctorVM> findByFilter(DoctorFilterVM filter, String language);
}
