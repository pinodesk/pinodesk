package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.entity.UserGroup;
import com.pinodesk.viewmodel.UserGroupFilterVM;

public interface UserGroupRepositoryCustom {

    List<UserGroup> findByFilter(UserGroupFilterVM filter);

    List<UserGroup> findByKeyword(String keyword);

}
