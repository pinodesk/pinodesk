package com.pinodesk.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.*;
import org.springframework.test.util.ReflectionTestUtils;
import com.pinodesk.constant.CommonConstants;
import com.pinodesk.constant.DomainError;
import com.pinodesk.constant.UserStatus;
import com.pinodesk.entity.User;
import com.pinodesk.exception.DomainException;
import com.pinodesk.repository.UserRepository;
import com.pinodesk.util.PasswordUtils;
import com.pinodesk.viewmodel.*;

class UserLifecycleTest {
    private UserRepository users;
    private UserService service;
    private ValidatorFactory validation;

    @BeforeEach
    void setUp() {
        users = mock(UserRepository.class);
        service = new UserService(mock(SessionService.class), users);
        validation = Validation.buildDefaultValidatorFactory();
        ReflectionTestUtils.setField(service, "validator", validation.getValidator());
    }

    @AfterEach
    void closeValidator() {
        validation.close();
    }

    private UserAddVM addition() {
        UserAddVM request = new UserAddVM();
        request.setFullName("New cashier");
        request.setUsername("cashier");
        request.setPassword("secret123");
        request.setStatus(UserStatus.ACTIVE);
        request.setUserGroupId(2L);
        return request;
    }

    private UserEditVM edit() {
        UserEditVM request = new UserEditVM();
        request.setFullName("Updated cashier");
        request.setUsername("cashier");
        request.setStatus(UserStatus.ACTIVE);
        request.setUserGroupId(2L);
        return request;
    }

    private User existing(Long group) {
        User user = new User();
        user.setId(8L);
        user.setUsername("cashier");
        user.setUserGroupId(group);
        user.setPasswordHash("existing-hash");
        when(users.findByIdAndDeletedAtIsNull(8L)).thenReturn(Optional.of(user));
        return user;
    }

    @Test
    void createPersistsProfileWithVerifiablePasswordHash() {
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        User saved = service.createUser(addition());
        assertEquals("New cashier", saved.getFullName());
        assertEquals("cashier", saved.getUsername());
        assertEquals(2L, saved.getUserGroupId());
        assertEquals("active", saved.getStatus());
        assertNotEquals("secret123", saved.getPasswordHash());
        assertTrue(PasswordUtils.isValid("secret123", saved.getPasswordHash()));
        verify(users).save(saved);
    }

    @Test
    void duplicateUsernameCannotBeCreated() {
        when(users.existsByUsernameAndDeletedAtIsNull("cashier")).thenReturn(true);
        DomainException error = assertThrows(DomainException.class, () -> service.createUser(addition()));
        assertEquals(DomainError.USER_EXISTS_BY_USERNAME, error.getError());
        verify(users, never()).save(any(User.class));
    }

    @Test
    void invalidInputNeverReachesRepository() {
        UserAddVM add = addition();
        add.setPassword("short");
        assertThrows(ConstraintViolationException.class, () -> service.createUser(add));
        UserEditVM edit = edit();
        edit.setUsername(" ");
        assertThrows(ConstraintViolationException.class, () -> service.updateUser(edit, 8L));
        verifyNoInteractions(users);
    }

    @Test
    void missingUserHasSpecificError() {
        DomainException error = assertThrows(DomainException.class, () -> service.updateUser(edit(), 8L));
        assertEquals(DomainError.USER_NOT_FOUND_BY_ID, error.getError());
        verify(users, never()).save(any(User.class));
    }

    @Test
    void unchangedUsernameAndNullPasswordPreserveCredentials() {
        User user = existing(2L);
        when(users.save(user)).thenReturn(user);
        assertSame(user, service.updateUser(edit(), 8L));
        assertEquals("Updated cashier", user.getFullName());
        assertEquals("existing-hash", user.getPasswordHash());
        verify(users, never()).existsByUsernameAndDeletedAtIsNull(anyString());
    }

    @Test
    void renameAndNewPasswordUpdateCredentials() {
        User user = existing(2L);
        UserEditVM request = edit();
        request.setUsername("renamed");
        request.setPassword("new-secret");
        service.updateUser(request, 8L);
        assertEquals("renamed", user.getUsername());
        assertTrue(PasswordUtils.isValid("new-secret", user.getPasswordHash()));
        verify(users).existsByUsernameAndDeletedAtIsNull("renamed");
        verify(users).save(user);
    }

    @Test
    void conflictingRenameDoesNotMutateOrSaveUser() {
        User user = existing(2L);
        UserEditVM request = edit();
        request.setUsername("taken");
        when(users.existsByUsernameAndDeletedAtIsNull("taken")).thenReturn(true);
        DomainException error = assertThrows(DomainException.class, () -> service.updateUser(request, 8L));
        assertEquals(DomainError.USER_EXISTS_BY_USERNAME, error.getError());
        assertEquals("cashier", user.getUsername());
        verify(users, never()).save(any(User.class));
    }

    @Test
    void lastAdministratorCannotChangeGroup() {
        User user = existing(CommonConstants.USER_GROUP_ID_ADMINISTRATOR);
        DomainException error = assertThrows(DomainException.class, () -> service.updateUser(edit(), 8L));
        assertEquals(DomainError.USER_GROUP_ADMINISTRATOR_MUST_HAVE_USER, error.getError());
        assertEquals(CommonConstants.USER_GROUP_ID_ADMINISTRATOR, user.getUserGroupId());
        verify(users, never()).save(any(User.class));
    }

    @Test
    void administratorCanChangeGroupWhenAnotherExists() {
        User user = existing(CommonConstants.USER_GROUP_ID_ADMINISTRATOR);
        when(users.existsByUserGroupIdAndIdNotAndDeletedAtIsNull(CommonConstants.USER_GROUP_ID_ADMINISTRATOR, 8L))
                .thenReturn(true);
        service.updateUser(edit(), 8L);
        assertEquals(2L, user.getUserGroupId());
        verify(users).save(user);
    }

    @Test
    void lastActiveAdministratorCannotBeDeactivated() {
        User user = existing(CommonConstants.USER_GROUP_ID_ADMINISTRATOR);
        UserEditVM request = edit();
        request.setUserGroupId(CommonConstants.USER_GROUP_ID_ADMINISTRATOR);
        request.setStatus(UserStatus.INACTIVE);
        DomainException error = assertThrows(DomainException.class, () -> service.updateUser(request, 8L));
        assertEquals(DomainError.USER_GROUP_ADMINISTRATOR_MUST_HAVE_USER, error.getError());
        verify(users, never()).save(user);
    }

    @Test
    void administratorCanBeDeactivatedWhenAnotherActiveOneExists() {
        User user = existing(CommonConstants.USER_GROUP_ID_ADMINISTRATOR);
        UserEditVM request = edit();
        request.setUserGroupId(CommonConstants.USER_GROUP_ID_ADMINISTRATOR);
        request.setStatus(UserStatus.INACTIVE);
        when(
                users.existsByUserGroupIdAndStatusAndIdNotAndDeletedAtIsNull(
                        CommonConstants.USER_GROUP_ID_ADMINISTRATOR,
                        "active",
                        8L))
                .thenReturn(true);
        service.updateUser(request, 8L);
        assertEquals("inactive", user.getStatus());
        verify(users).save(user);
    }

    @Test
    void searchReturnsRepositoryResultsForRequestedFilter() {
        UserFilterVM filter = new UserFilterVM();
        List<UserVM> results = List.of(new UserVM());
        when(users.findByFilter(filter)).thenReturn(results);
        assertSame(results, service.searchUsersByFilter(filter));
    }
}
