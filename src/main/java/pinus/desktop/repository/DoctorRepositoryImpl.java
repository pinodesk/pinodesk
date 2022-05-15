package pinus.desktop.repository;

import java.util.List;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.sql.Where;

import org.springframework.stereotype.Repository;

import pinus.desktop.domain.Doctor;

@Repository
public class DoctorRepositoryImpl extends AbstractRepository<Doctor> implements DoctorRepository {

    @Override
    public List<Doctor> findByKeyword(String keyword) {
        return read(
                new Where().containsIgnoreCase(Doctor.C_NAME, keyword).contains(Doctor.C_REGISTRATION_NUMBER, keyword)
                        .contains(Doctor.C_MEDICAL_LICENSE_NUMBER, keyword));
    }

}
