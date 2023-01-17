package pospino.desktop.repository;

import java.util.List;

import pospino.desktop.domain.UserGroup;
import pospino.desktop.viewmodel.UserGroupFilterVM;

public interface UserGroupRepositoryCustom {

    List<UserGroup> findByFilter(UserGroupFilterVM filter);

    List<UserGroup> findByKeyword(String keyword);

}
