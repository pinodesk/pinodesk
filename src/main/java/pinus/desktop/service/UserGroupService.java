package pinus.desktop.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.domain.UserGroup;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.UserGroupRepository;
import pinus.desktop.viewmodel.UserGroupFilterVM;
import pinus.desktop.viewmodel.UserGroupVM;

@Service
public class UserGroupService extends BaseService {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Cacheable(CacheNameConstants.USER_GROUPS_BY_FILTER)
    public List<UserGroupVM> searchUserGroupsByFilter(UserGroupFilterVM filter) {
        return objectConverter.convertList(userGroupRepository.findByFilter(filter), UserGroupVM.class);
    }

    @CacheEvict(value = { CacheNameConstants.USER_GROUPS_BY_FILTER, CacheNameConstants.USER_GROUPS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeUserGroups(List<Long> ids) {
        if (ids.contains(CommonConstants.USER_GROUP_ID_ADMINISTRATOR)) {
            throw new DomainException(DomainError.DELETE_USER_GROUP_ADMINISTRATOR_FORBIDDEN);
        }
        userGroupRepository.deleteUpdateByIdIn(ids);
    }

    @Cacheable(CacheNameConstants.USER_GROUPS_BY_KEYWORD)
    public List<UserGroupVM> searchUserGroupsByKeyword(String keyword) {
        List<UserGroup> userGroups = StringUtils.isBlank(keyword) ?
                userGroupRepository.findByDeletedAtIsNull() : userGroupRepository.findByKeyword(keyword.trim());
        return objectConverter.convertList(userGroups, UserGroupVM.class);
    }

}
