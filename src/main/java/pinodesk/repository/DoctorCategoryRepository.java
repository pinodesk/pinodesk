package pinodesk.repository;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinodesk.entity.DoctorCategory;

@Repository
public interface DoctorCategoryRepository
        extends PagingAndSortingRepository<DoctorCategory, Long>, DoctorCategoryRepositoryCustom {

    Optional<DoctorCategory> findByIdAndDeletedAtIsNull(Long id);

}
