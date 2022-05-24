package pinus.desktop.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.DoctorCategory;

@Repository
public interface DoctorCategoryRepository extends PagingAndSortingRepository<DoctorCategory, Long> {

}
