package pinus.desktop.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.domain.UserGroup;

@Repository
public interface UserGroupRepository extends PagingAndSortingRepository<UserGroup, Long>, UserGroupRepositoryCustom {

    @Transactional
    @Modifying
    @Query("update user_group set updated_at=now(), deleted_at=now() where id in (:ids)")
    void deleteUpdateByIdIn(List<Long> ids);

}
