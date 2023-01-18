package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.viewmodel.UserGroupMenuVM;

public interface UserGroupMenuRepositoryCustom {

    List<UserGroupMenuVM> findByUserGroupId(Long userGroupId, String language);
}
