package pinodesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import pinodesk.domain.Unit;

@Repository
public interface UnitRepository extends PagingAndSortingRepository<Unit, Long>, UnitRepositoryCustom {

    List<Unit> findByDeletedAtIsNull();

    Optional<Unit> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByCodeAndDeletedAtIsNull(String code);

    Optional<Unit> findByLabelAndDeletedAtIsNull(String label);

    List<Unit> findByLanguageAndDeletedAtIsNullOrderByName(String language);

    Optional<Unit> findByLanguageAndCodeAndDeletedAtIsNull(String language, String code);

}
