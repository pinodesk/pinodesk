package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.UserGroupMenuVM;

public interface UserGroupMenuRepositoryCustom {

    List<UserGroupMenuVM> findByUserGroupId(Long userGroupId, String language);
}
