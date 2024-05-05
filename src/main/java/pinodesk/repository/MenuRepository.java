package pinodesk.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinodesk.entity.Menu;

@Repository
public interface MenuRepository extends PagingAndSortingRepository<Menu, Long> {

    List<Menu> findByLanguageAndDeletedAtIsNullOrderBySeqNum(String language);
}
