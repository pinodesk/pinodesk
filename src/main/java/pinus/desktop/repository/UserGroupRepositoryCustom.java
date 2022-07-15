package pinus.desktop.repository;

import java.util.List;

import pinus.desktop.domain.UserGroup;
import pinus.desktop.viewmodel.UserGroupFilterVM;

public interface UserGroupRepositoryCustom {

    List<UserGroup> findByFilter(UserGroupFilterVM filter);

    List<UserGroup> findByKeyword(String keyword);

}
