package pinus.desktop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pinus.desktop.constant.CacheNameConstants;
import pinus.desktop.constant.CommonConstants;
import pinus.desktop.constant.DomainError;
import pinus.desktop.constant.SimpleStatus;
import pinus.desktop.domain.Menu;
import pinus.desktop.domain.UserGroup;
import pinus.desktop.domain.UserGroupMenu;
import pinus.desktop.exception.DomainException;
import pinus.desktop.repository.MenuRepository;
import pinus.desktop.repository.UserGroupMenuRepository;
import pinus.desktop.repository.UserGroupRepository;
import pinus.desktop.repository.UserRepository;
import pinus.desktop.viewmodel.UserGroupAddVM;
import pinus.desktop.viewmodel.UserGroupEditVM;
import pinus.desktop.viewmodel.UserGroupFilterVM;
import pinus.desktop.viewmodel.UserGroupMenuVM;
import pinus.desktop.viewmodel.UserGroupVM;

@Service
public class UserGroupService extends BaseService {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserGroupMenuRepository userGroupMenuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Cacheable(CacheNameConstants.USER_GROUPS_BY_FILTER)
    public List<UserGroupVM> searchUserGroupsByFilter(UserGroupFilterVM filter) {
        return objectConverter.convertList(userGroupRepository.findByFilter(filter), UserGroupVM.class);
    }

    @CacheEvict(value = { CacheNameConstants.USER_GROUPS_BY_FILTER, CacheNameConstants.USER_GROUPS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public void removeUserGroups(List<Long> ids) {
        if (ids.contains(CommonConstants.USER_GROUP_ID_ADMINISTRATOR)) {
            throw new DomainException(DomainError.USER_GROUP_ADMINISTRATOR_MODIFICATION_FORBIDDEN);
        }
        ids.forEach(id -> {
            UserGroup userGroup = userGroupRepository.findByIdAndDeletedAtIsNull(id)
                    .orElseThrow(() -> new DomainException(DomainError.USER_GROUP_NOT_FOUND_BY_ID));
            Long count = userRepository.countByUserGroupIdAndDeletedAtIsNull(id);
            if (count > 0) {
                throw new DomainException(DomainError.DELETE_USER_GROUP_USER_EXISTS, userGroup.getName(), count);
            }
        });
        userGroupRepository.deleteUpdateByIdIn(ids);
    }

    @Cacheable(CacheNameConstants.USER_GROUPS_BY_KEYWORD)
    public List<UserGroupVM> searchUserGroupsByKeyword(String keyword) {
        List<UserGroup> userGroups = StringUtils.isBlank(keyword) ?
                userGroupRepository.findByDeletedAtIsNull() : userGroupRepository.findByKeyword(keyword.trim());
        return objectConverter.convertList(userGroups, UserGroupVM.class);
    }

    public UserGroupVM getUserGroupById(Long id) {
        return objectConverter.convertOptionalOrThrow(
                userGroupRepository.findByIdAndDeletedAtIsNull(id),
                UserGroupVM.class,
                new DomainException(DomainError.USER_GROUP_NOT_FOUND_BY_ID));
    }

    public List<UserGroupMenuVM> getUserGroupMenusByUserGroupId(Long userGroupId, String language) {
        List<Menu> menus = menuRepository.findByLanguageAndDeletedAtIsNull(language);
        List<UserGroupMenuVM> userGroupMenus = userGroupMenuRepository.findByUserGroupId(userGroupId, language);
        List<UserGroupMenuVM> ordered = new ArrayList<>();
        menus.forEach(menu -> {
            if (menu.getParentMenuId() == null) {
                UserGroupMenuVM ugm = userGroupMenus.stream().filter(u -> u.getMenuId().equals(menu.getId())).findAny()
                        .orElse(toUserGroupMenu(menu));
                ordered.add(ugm);
                menus.forEach(menu1 -> {
                    if (Objects.equals(menu1.getParentMenuId(), ugm.getMenuId())) {
                        UserGroupMenuVM child = userGroupMenus.stream().filter(u -> u.getMenuId().equals(menu1.getId()))
                                .findAny().orElse(toUserGroupMenu(menu1));
                        ordered.add(child);
                    }
                });
            }
        });
        return ordered;
    }

    public List<UserGroupMenuVM> getUserGroupMenus(String language) {
        List<Menu> menus = menuRepository.findByLanguageAndDeletedAtIsNull(language);
        List<UserGroupMenuVM> ordered = new ArrayList<>();
        menus.forEach(menu -> {
            if (menu.getParentMenuId() == null) {
                ordered.add(toUserGroupMenu(menu));
                menus.forEach(menu1 -> {
                    if (Objects.equals(menu.getId(), menu1.getParentMenuId())) {
                        ordered.add(toUserGroupMenu(menu1));
                    }
                });
            }
        });
        return ordered;
    }

    private UserGroupMenuVM toUserGroupMenu(Menu menu) {
        UserGroupMenuVM ugm = new UserGroupMenuVM();
        ugm.setLanguage(menu.getLanguage());
        ugm.setMenuCode(menu.getCode());
        ugm.setMenuId(menu.getId());
        ugm.setMenuName(menu.getName());
        ugm.setParentMenuId(menu.getParentMenuId());
        ugm.setRead(SimpleStatus.NO.toString());
        ugm.setWrite(SimpleStatus.NO.toString());
        return ugm;
    }

    @CacheEvict(value = { CacheNameConstants.USER_GROUPS_BY_FILTER, CacheNameConstants.USER_GROUPS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public UserGroup createUserGroup(UserGroupAddVM userGroupAdd) {
        if (userGroupRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(userGroupAdd.getName())) {
            throw new DomainException(DomainError.USER_EXISTS_BY_USERNAME);
        }
        UserGroup ug = new UserGroup();
        ug.setName(userGroupAdd.getName());
        ug.setDescription(userGroupAdd.getDescription());
        ug.setStatus(userGroupAdd.getStatus().toString());
        UserGroup created = userGroupRepository.save(ug);
        List<UserGroupMenu> userGroupMenus = new ArrayList<>();
        userGroupAdd.getUserGroupMenus().forEach(vm -> {
            UserGroupMenu ugm = new UserGroupMenu();
            ugm.setMenuCode(vm.getMenuCode());
            ugm.setRead(vm.getRead());
            ugm.setUserGroupId(created.getId());
            ugm.setWrite(vm.getWrite());
            userGroupMenus.add(ugm);
        });
        userGroupMenuRepository.saveAll(userGroupMenus);
        return created;
    }

    @CacheEvict(value = { CacheNameConstants.USER_GROUPS_BY_FILTER, CacheNameConstants.USER_GROUPS_BY_KEYWORD },
        allEntries = true)
    @Transactional
    public UserGroup updateUserGroup(UserGroupEditVM userGroupEdit, Long userGroupId) {
        if (CommonConstants.USER_GROUP_ID_ADMINISTRATOR.equals(userGroupId)) {
            throw new DomainException(DomainError.USER_GROUP_ADMINISTRATOR_MODIFICATION_FORBIDDEN);
        }
        UserGroup userGroup = userGroupRepository.findByIdAndDeletedAtIsNull(userGroupId)
                .orElseThrow(() -> new DomainException(DomainError.USER_GROUP_NOT_FOUND_BY_ID));
        if (!userGroup.getName().equalsIgnoreCase(userGroupEdit.getName())
                && userGroupRepository.existsByNameIgnoreCaseAndDeletedAtIsNull(userGroupEdit.getName())) {
            throw new DomainException(DomainError.USER_GROUP_EXISTS_BY_NAME);
        }
        userGroup.setName(userGroupEdit.getName());
        userGroup.setStatus(userGroupEdit.getStatus().toString());
        userGroup.setDescription(userGroupEdit.getDescription());
        UserGroup updated = userGroupRepository.save(userGroup);
        userGroupMenuRepository.deleteByUserGroupId(userGroupId);
        List<UserGroupMenu> userGroupMenus = new ArrayList<>();
        userGroupEdit.getUserGroupMenus().forEach(vm -> {
            UserGroupMenu ugm = new UserGroupMenu();
            ugm.setMenuCode(vm.getMenuCode());
            ugm.setRead(vm.getRead());
            ugm.setUserGroupId(userGroupId);
            ugm.setWrite(vm.getWrite());
            userGroupMenus.add(ugm);
        });
        userGroupMenuRepository.saveAll(userGroupMenus);
        return updated;
    }

}
