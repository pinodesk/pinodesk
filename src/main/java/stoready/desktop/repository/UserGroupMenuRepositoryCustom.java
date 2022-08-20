package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.viewmodel.UserGroupMenuVM;

public interface UserGroupMenuRepositoryCustom {

    List<UserGroupMenuVM> findByUserGroupId(Long userGroupId, String language);
}
