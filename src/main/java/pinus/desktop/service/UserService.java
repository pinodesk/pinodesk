package pinus.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.UserRepository;
import pinus.desktop.viewmodel.UserFilterVM;
import pinus.desktop.viewmodel.UserVM;

@Service
public class UserService extends BaseService {

    @Autowired
    private UserRepository userRepository;

    @Cacheable(CacheNameConstants.USERS_BY_FILTER)
    public List<UserVM> searchUsersByFilter(UserFilterVM filter) {
        return objectConverter.convertList(userRepository.findByFilter(filter), UserVM.class);
    }

    @CacheEvict(value = { CacheNameConstants.USERS_BY_FILTER }, allEntries = true)
    @Transactional
    public void removeUsers(List<Long> ids) {
        userRepository.deleteUpdateByIdIn(ids);
        if (!userRepository.existsByUserGroupIdAndDeletedAtIsNull(CommonConstants.USER_GROUP_ID_ADMINISTRATOR)) {
            throw new DomainException(DomainError.USER_GROUP_ADMINISTRATOR_MUST_HAVE_USER);
        }
    }

}
