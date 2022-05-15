package pinus.desktop.repository;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.DoctorCategory;

@Repository
public class DoctorCategoryRepositoryImpl extends AbstractRepository<DoctorCategory>
        implements DoctorCategoryRepository {

}
