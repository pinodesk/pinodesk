package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import com.pinodesk.constant.DomainError;
import com.pinodesk.constant.SimpleStatus;
import com.pinodesk.constant.UserGroupStatus;
import com.pinodesk.entity.Menu;
import com.pinodesk.entity.UserGroup;
import com.pinodesk.entity.UserGroupMenu;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.*;
import com.pinodesk.viewmodel.*;

class UserGroupServiceTest extends BaseServiceTest {
    @Mock
    private UserGroupRepository groups;
    @Mock
    private UserGroupMenuRepository permissions;
    @Mock
    private UserRepository users;
    @Mock
    private MenuRepository menus;
    @InjectMocks
    private UserGroupService service;

    private UserGroup group() {
        UserGroup group = new UserGroup();
        group.setId(2L);
        group.setName("Cashiers");
        return group;
    }

    private Menu menu(long id, Long parent) {
        Menu menu = new Menu();
        menu.setId(id);
        menu.setParentMenuId(parent);
        menu.setCode("M" + id);
        menu.setName("Menu " + id);
        menu.setLanguage("id");
        return menu;
    }

    private UserGroupMenuVM permission() {
        UserGroupMenuVM vm = new UserGroupMenuVM();
        vm.setMenuId(11L);
        vm.setMenuCode("M11");
        vm.setRead(SimpleStatus.YES.toString());
        vm.setWrite(SimpleStatus.NO.toString());
        return vm;
    }

    @Test
    void administratorCannotBeRemovedOrEdited() {
        assertEquals(
                DomainError.USER_GROUP_ADMINISTRATOR_MODIFICATION_FORBIDDEN,
                assertThrows(DomainException.class, () -> service.removeUserGroups(List.of(1L, 2L))).getError());
        assertEquals(
                DomainError.USER_GROUP_ADMINISTRATOR_MODIFICATION_FORBIDDEN,
                assertThrows(DomainException.class, () -> service.updateUserGroup(new UserGroupEditVM(), 1L))
                        .getError());
        verifyNoInteractions(groups, users, permissions);
    }

    @Test
    void missingGroupRejectsBatchBeforeDelete() {
        when(groups.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(group()));
        when(users.countByUserGroupIdAndDeletedAtIsNull(2L)).thenReturn(0L);
        assertEquals(
                DomainError.USER_GROUP_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.removeUserGroups(List.of(2L, 3L))).getError());
        verify(groups, never()).deleteUpdateByIdIn(any());
    }

    @Test
    void occupiedGroupCannotBeRemoved() {
        when(groups.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(group()));
        when(users.countByUserGroupIdAndDeletedAtIsNull(2L)).thenReturn(2L);
        assertEquals(
                DomainError.DELETE_USER_GROUP_USER_EXISTS,
                assertThrows(DomainException.class, () -> service.removeUserGroups(List.of(2L))).getError());
        verify(groups, never()).deleteUpdateByIdIn(any());
    }

    @Test
    void emptyGroupCanBeRemoved() {
        when(groups.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(group()));
        when(users.countByUserGroupIdAndDeletedAtIsNull(2L)).thenReturn(0L);
        service.removeUserGroups(List.of(2L));
        verify(groups).deleteUpdateByIdIn(List.of(2L));
    }

    @Test
    void searchTrimsKeywordAndMapsResults() {
        when(groups.findByKeyword("cash")).thenReturn(List.of(group()));
        assertEquals("Cashiers", service.searchUserGroupsByKeyword(" cash ").get(0).getName());
        for (String keyword : new String[] { null, "", "  " }) {
            assertTrue(service.searchUserGroupsByKeyword(keyword).isEmpty());
        }
        verify(groups, times(3)).findByDeletedAtIsNull();
        UserGroupFilterVM filter = new UserGroupFilterVM();
        when(groups.findByFilter(filter)).thenReturn(List.of(group()));
        assertEquals(2L, service.searchUserGroupsByFilter(filter).get(0).getId());
    }

    @Test
    void lookupMapsGroupAndMissingIdRaisesDomainError() {
        when(groups.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(group()));
        assertEquals("Cashiers", service.getUserGroupById(2L).getName());
        assertEquals(
                DomainError.USER_GROUP_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.getUserGroupById(3L)).getError());
    }

    @Test
    void menuTreePreservesStoredPermissionAndDefaultsMissingPermissionsToNo() {
        when(menus.findByLanguageAndDeletedAtIsNullOrderBySeqNum("id"))
                .thenReturn(List.of(menu(11, 10L), menu(10, null), menu(20, null), menu(21, 20L), menu(99, 88L)));
        UserGroupMenuVM granted = permission();
        when(permissions.findByUserGroupId(2L, "id")).thenReturn(List.of(granted));
        List<UserGroupMenuVM> result = service.getUserGroupMenusByUserGroupId(2L, "id");
        assertEquals(
                List.of(10L, 11L, 20L, 21L),
                result.stream().map(UserGroupMenuVM::getMenuId).collect(Collectors.toList()));
        assertSame(granted, result.get(1));
        assertEquals("id", result.get(0).getLanguage());
        assertEquals("M10", result.get(0).getMenuCode());
        assertEquals("Menu 10", result.get(0).getMenuName());
        assertEquals(SimpleStatus.NO.toString(), result.get(0).getRead());
        assertEquals(SimpleStatus.NO.toString(), result.get(3).getWrite());
        List<UserGroupMenuVM> defaults = service.getUserGroupMenus("id");
        assertEquals(
                List.of(10L, 11L, 20L, 21L),
                defaults.stream().map(UserGroupMenuVM::getMenuId).collect(Collectors.toList()));
        assertTrue(
                defaults.stream().allMatch(
                        p -> SimpleStatus.NO.toString().equals(p.getRead())
                                && SimpleStatus.NO.toString().equals(p.getWrite())));
    }

    @Test
    void creationPersistsPermissionsUsingSavedGroupId() {
        UserGroupAddVM add = new UserGroupAddVM();
        add.setName("Cashiers");
        add.setDescription("Checkout access");
        add.setStatus(UserGroupStatus.ACTIVE);
        add.setUserGroupMenus(List.of(permission()));
        when(groups.save(any(UserGroup.class))).thenAnswer(inv -> {
            UserGroup value = inv.getArgument(0);
            value.setId(7L);
            return value;
        });
        UserGroup saved = service.createUserGroup(add);
        assertEquals("Cashiers", saved.getName());
        assertEquals("Checkout access", saved.getDescription());
        assertEquals(UserGroupStatus.ACTIVE.toString(), saved.getStatus());
        ArgumentCaptor<List<UserGroupMenu>> captor = ArgumentCaptor.forClass(List.class);
        verify(permissions).saveAll(captor.capture());
        UserGroupMenu access = captor.getValue().get(0);
        assertEquals(7L, access.getUserGroupId());
        assertEquals("M11", access.getMenuCode());
        assertEquals(SimpleStatus.YES.toString(), access.getRead());
        assertEquals(SimpleStatus.NO.toString(), access.getWrite());
    }

    @Test
    void duplicateNameCreationDoesNotWrite() {
        UserGroupAddVM add = new UserGroupAddVM();
        add.setName("Cashiers");
        when(groups.existsByNameIgnoreCaseAndDeletedAtIsNull("Cashiers")).thenReturn(true);
        assertThrows(DomainException.class, () -> service.createUserGroup(add));
        verify(groups, never()).save(any());
        verifyNoInteractions(permissions);
    }

    private UserGroupEditVM edit(String name) {
        UserGroupEditVM edit = new UserGroupEditVM();
        edit.setName(name);
        edit.setDescription("Updated");
        edit.setStatus(UserGroupStatus.ACTIVE);
        edit.setUserGroupMenus(List.of(permission()));
        return edit;
    }

    @Test
    void missingOrConflictingEditDoesNotWrite() {
        assertEquals(
                DomainError.USER_GROUP_NOT_FOUND_BY_ID,
                assertThrows(DomainException.class, () -> service.updateUserGroup(edit("Other"), 2L)).getError());
        when(groups.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(group()));
        when(groups.existsByNameIgnoreCaseAndDeletedAtIsNull("Other")).thenReturn(true);
        assertEquals(
                DomainError.USER_GROUP_EXISTS_BY_NAME,
                assertThrows(DomainException.class, () -> service.updateUserGroup(edit("Other"), 2L)).getError());
        verify(groups, never()).save(any());
        verifyNoInteractions(permissions);
    }

    @Test
    void editReplacesPermissionsAndKeepsGroupId() {
        UserGroup original = group();
        when(groups.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(original));
        when(groups.save(original)).thenReturn(original);
        assertSame(original, service.updateUserGroup(edit("CASHIERS"), 2L));
        verify(groups, never()).existsByNameIgnoreCaseAndDeletedAtIsNull(anyString());
        assertEquals("Updated", original.getDescription());
        ArgumentCaptor<List<UserGroupMenu>> captor = ArgumentCaptor.forClass(List.class);
        org.mockito.InOrder order = inOrder(groups, permissions);
        order.verify(groups).save(original);
        order.verify(permissions).deleteByUserGroupId(2L);
        order.verify(permissions).saveAll(captor.capture());
        assertEquals(2L, captor.getValue().get(0).getUserGroupId());
        assertEquals("M11", captor.getValue().get(0).getMenuCode());
    }

    @Test
    void uniqueRenameIsAllowed() {
        UserGroup original = group();
        when(groups.findByIdAndDeletedAtIsNull(2L)).thenReturn(Optional.of(original));
        service.updateUserGroup(edit("Supervisors"), 2L);
        assertEquals("Supervisors", original.getName());
        verify(groups).existsByNameIgnoreCaseAndDeletedAtIsNull("Supervisors");
        verify(groups).save(original);
    }
}
