package pinodesk.repository;

import java.util.List;

import pinodesk.entity.UserGroup;
import pinodesk.viewmodel.UserGroupFilterVM;

public interface UserGroupRepositoryCustom {

    List<UserGroup> findByFilter(UserGroupFilterVM filter);

    List<UserGroup> findByKeyword(String keyword);

}
