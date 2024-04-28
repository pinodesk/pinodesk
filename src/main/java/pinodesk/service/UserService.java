package pinodesk.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinodesk.annotation.ForActivity;
import pinodesk.constant.Activity;
import pinodesk.constant.CacheNameConstants;
import pinodesk.constant.CommonConstants;
import pinodesk.constant.DomainError;
import pinodesk.constant.UserStatus;
import pinodesk.entity.User;
import pinodesk.exception.DomainException;
import pinodesk.repository.UserRepository;
import pinodesk.viewmodel.UserAddVM;
import pinodesk.viewmodel.UserEditVM;
import pinodesk.viewmodel.UserFilterVM;
import pinodesk.viewmodel.UserVM;
import pinodesk.util.PasswordUtils;

@Service
public class UserService extends BaseService {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserRepository userRepository;

    @ForActivity(Activity.SEARCH_USERS_BY_FILTER)
    @Cacheable(CacheNameConstants.USERS_BY_FILTER)
    public List<UserVM> searchUsersByFilter(UserFilterVM filter) {
        return userRepository.findByFilter(filter);
    }

    @ForActivity(Activity.REMOVE_USERS)
    @CacheEvict(value = { CacheNameConstants.USERS_BY_FILTER }, allEntries = true)
    @Transactional
    public void removeUsers(List<Long> ids) {
        UserVM currentUser = sessionService.getCurrentSession().getUser();
        if (ids.contains(currentUser.getId())) {
            throw new DomainException(DomainError.DELETE_CURRENT_USER_FORBIDDEN);
        }
        userRepository.deleteUpdateByIdIn(ids);
        if (!userRepository.existsByUserGroupIdAndStatusAndDeletedAtIsNull(
                CommonConstants.USER_GROUP_ID_ADMINISTRATOR,
                UserStatus.ACTIVE.toString())) {
            throw new DomainException(DomainError.USER_GROUP_ADMINISTRATOR_MUST_HAVE_USER);
        }
    }

    @ForActivity(Activity.ADD_USER)
    @CacheEvict(value = { CacheNameConstants.USERS_BY_FILTER }, allEntries = true)
    @Transactional
    public User createUser(UserAddVM userAdd) {
        validateConstraints(userAdd);
        if (userRepository.existsByUsernameAndDeletedAtIsNull(userAdd.getUsername())) {
            throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
        }
        User user = new User();
        user.setFullName(userAdd.getFullName());
        user.setUsername(userAdd.getUsername());
        user.setStatus(userAdd.getStatus().toString());
        user.setUserGroupId(userAdd.getUserGroupId());
        user.setPasswordHash(PasswordUtils.encrypt(userAdd.getPassword()));
        return userRepository.save(user);
    }

    @ForActivity(Activity.EDIT_USER)
    @CacheEvict(value = { CacheNameConstants.USERS_BY_FILTER }, allEntries = true)
    @Transactional
    public User updateUser(UserEditVM userEdit, Long userId) {
        validateConstraints(userEdit);
        String username = userEdit.getUsername();
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new DomainException(DomainError.USER_NOT_FOUND_BY_ID));
        if (!user.getUsername().equals(username) && userRepository.existsByUsernameAndDeletedAtIsNull(username)) {
            throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
        }
        checkAdministratorUserGroup(user, userEdit);
        user.setFullName(userEdit.getFullName());
        user.setUsername(userEdit.getUsername());
        user.setStatus(userEdit.getStatus().toString());
        user.setUserGroupId(userEdit.getUserGroupId());
        if (StringUtils.isNotBlank(userEdit.getPassword())) {
            user.setPasswordHash(PasswordUtils.encrypt(userEdit.getPassword()));
        }
        return userRepository.save(user);
    }

    private void checkAdministratorUserGroup(User user, UserEditVM userEdit) {
        if (CommonConstants.USER_GROUP_ID_ADMINISTRATOR.equals(user.getUserGroupId())
                && !userEdit.getUserGroupId().equals(user.getUserGroupId())
                && !userRepository.existsByUserGroupIdAndIdNotAndDeletedAtIsNull(
                        CommonConstants.USER_GROUP_ID_ADMINISTRATOR,
                        user.getId())) {
            throw new DomainException(DomainError.USER_GROUP_ADMINISTRATOR_MUST_HAVE_USER);
        }
        if (CommonConstants.USER_GROUP_ID_ADMINISTRATOR.equals(userEdit.getUserGroupId())
                && UserStatus.INACTIVE.equals(userEdit.getStatus())
                && !userRepository.existsByUserGroupIdAndStatusAndIdNotAndDeletedAtIsNull(
                        userEdit.getUserGroupId(),
                        UserStatus.ACTIVE.toString(),
                        user.getId())) {
            throw new DomainException(DomainError.USER_GROUP_ADMINISTRATOR_MUST_HAVE_USER);
        }
    }

}
