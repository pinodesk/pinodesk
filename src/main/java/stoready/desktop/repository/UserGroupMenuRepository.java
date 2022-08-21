package stoready.desktop.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import stoready.desktop.domain.UserGroupMenu;

@Repository
public interface UserGroupMenuRepository
        extends PagingAndSortingRepository<UserGroupMenu, Long>, UserGroupMenuRepositoryCustom {

    @Transactional
    @Modifying
    @Query("delete from user_group_menu where user_group_id = :userGroupId")
    Long deleteByUserGroupId(@Param("userGroupId") Long userGroupId);

}
