package stoready.desktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import stoready.desktop.domain.Unit;

@Repository
public interface UnitRepository extends PagingAndSortingRepository<Unit, Long>, UnitRepositoryCustom {

    List<Unit> findByDeletedAtIsNull();

    Optional<Unit> findByIdAndDeletedAtIsNull(Long id);

}
