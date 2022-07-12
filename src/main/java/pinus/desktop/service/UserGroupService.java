package pinus.desktop.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import pinus.desktop.constant.CacheNameConstants;
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

}
