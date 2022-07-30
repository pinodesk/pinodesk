package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.viewmodel.UserGroupMenuVM;

public interface UserGroupMenuRepositoryCustom {

    List<UserGroupMenuVM> findByUserGroupId(Long userGroupId, String language);
}
