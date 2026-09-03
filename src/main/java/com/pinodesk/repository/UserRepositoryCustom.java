package com.pinodesk.repository;

import java.util.List;

import com.pinodesk.viewmodel.UserFilterVM;
import com.pinodesk.viewmodel.UserVM;

public interface UserRepositoryCustom {

    List<UserVM> findByFilter(UserFilterVM filter);
}
