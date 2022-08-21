package stoready.desktop.repository;

import java.util.List;

import stoready.desktop.domain.UserGroup;
import stoready.desktop.viewmodel.UserGroupFilterVM;

public interface UserGroupRepositoryCustom {

    List<UserGroup> findByFilter(UserGroupFilterVM filter);

    List<UserGroup> findByKeyword(String keyword);

}
