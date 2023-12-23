package pinodesk.repository;

import java.util.List;

import pinodesk.viewmodel.UserGroupMenuVM;

public interface UserGroupMenuRepositoryCustom {

    List<UserGroupMenuVM> findByUserGroupId(Long userGroupId, String language);
}
