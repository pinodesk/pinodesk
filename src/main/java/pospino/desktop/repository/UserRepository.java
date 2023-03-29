package pospino.desktop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pospino.desktop.domain.User;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long>, UserRepositoryCustom {

    @Transactional
    @Modifying
    @Query("update `user` set updated_at=now(), deleted_at=now() where id in (:ids)")
    void deleteUpdateByIdIn(@Param("ids") List<Long> ids);

    boolean existsByUserGroupIdAndIdNotAndDeletedAtIsNull(Long userGroupId, Long id);

    boolean existsByUserGroupIdAndStatusAndDeletedAtIsNull(Long userGroupId, String status);

    boolean existsByUsernameAndDeletedAtIsNull(String username);

    boolean existsByUserGroupIdAndStatusAndIdNotAndDeletedAtIsNull(Long userGroupId, String status, Long userId);

    Optional<User> findByIdAndDeletedAtIsNull(Long id);

    Long countByUserGroupIdAndDeletedAtIsNull(Long userGroupId);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);
}
