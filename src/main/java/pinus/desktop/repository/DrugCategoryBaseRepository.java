package pinus.desktop.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinus.desktop.domain.DrugCategoryBase;

@Repository
public interface DrugCategoryBaseRepository extends PagingAndSortingRepository<DrugCategoryBase, Long> {

    List<DrugCategoryBase> findByDeletedAtIsNull();

}
