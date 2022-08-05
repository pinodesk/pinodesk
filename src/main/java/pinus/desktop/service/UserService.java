package pinus.desktop.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.annotation.ForActivity;
import pinus.desktop.constant.Activity;
import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.constant.UserStatus;
import pinus.desktop.domain.User;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.UserRepository;
import pinus.desktop.util.PasswordUtils;
import pinus.desktop.viewmodel.UserAddVM;
import pinus.desktop.viewmodel.UserEditVM;
import pinus.desktop.viewmodel.UserFilterVM;
import pinus.desktop.viewmodel.UserVM;

@Service
public class UserService extends BaseService {

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
