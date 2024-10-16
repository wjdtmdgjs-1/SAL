package com.sparta.sal.domain.user;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.user.dto.request.UserChangePasswordRequest;
import com.sparta.sal.domain.user.dto.response.UserResponse;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.user.repository.UserRepository;
import com.sparta.sal.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void getUser_validUser() {

        long userId = 1L;
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "email", "test@example.com");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        UserResponse response = userService.getUser(userId);

        assertNotNull(response);
    }

    @Test
    void getUser_invalidUser() {
        long userId = 1L;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.getUser(userId));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void withdrawUser_validUser() {
        long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "test@example.com", UserRole.ROLE_USER);
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "userStatus", true);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        userService.withdrawUser(authUser);

        assertFalse(user.getUserStatus());
    }

    @Test
    void withdrawUser_invalidUser() {
        long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "test@example.com", UserRole.ROLE_USER);
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.withdrawUser(authUser));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void changePassword_success() {
        long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "test@example.com", UserRole.ROLE_USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "oldPassword");
        ReflectionTestUtils.setField(request, "newPassword", "newPassword");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "password", "encodedOldPassword");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("oldPassword", user.getPassword())).willReturn(true);
        given(passwordEncoder.matches("newPassword", user.getPassword())).willReturn(false);
        given(passwordEncoder.encode("newPassword")).willReturn("encodedNewPassword");

        userService.changePassword(authUser, request);

        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void changePassword_invalidOldPassword() {
        long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "test@example.com", UserRole.ROLE_USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "wrongOldPassword");
        ReflectionTestUtils.setField(request, "newPassword", "newPassword");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "password", "encodedOldPassword");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongOldPassword", user.getPassword())).willReturn(false);
        given(passwordEncoder.matches("newPassword", user.getPassword())).willReturn(false);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(authUser, request));
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    void changePassword_sameOldAndNewPassword() {
        long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "test@example.com", UserRole.ROLE_USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest();
        ReflectionTestUtils.setField(request, "oldPassword", "samePassword");
        ReflectionTestUtils.setField(request, "newPassword", "samePassword");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "password", "encodedSamePassword");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("samePassword", user.getPassword())).willReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.changePassword(authUser, request));
        assertEquals("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.", exception.getMessage());
    }
}

