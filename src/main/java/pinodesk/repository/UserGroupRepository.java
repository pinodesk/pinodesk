package pinodesk.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.domain.UserGroup;

@Repository
public interface UserGroupRepository extends PagingAndSortingRepository<UserGroup, Long>, UserGroupRepositoryCustom {

    @Transactional
    @Modifying
    @Query("update user_group set updated_at=now(), deleted_at=now() where id in (:ids)")
    void deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    List<UserGroup> findByDeletedAtIsNull();

    Optional<UserGroup> findByIdAndDeletedAtIsNull(Long id);

    boolean existsByNameIgnoreCaseAndDeletedAtIsNull(String name);

}
