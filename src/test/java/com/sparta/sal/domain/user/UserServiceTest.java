package com.sparta.sal.domain.user;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.dto.request.UserChangePasswordRequest;
import com.sparta.sal.domain.user.dto.request.UserWithdrawRequest;
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

    @Mock
    private MemberRepository memberRepository;

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
        UserWithdrawRequest request = new UserWithdrawRequest("password");

        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches(request.getPassword(), user.getPassword())).willReturn(true);

        userService.withdrawUser(authUser, request);

        assertFalse(user.getUserStatus());
    }

    @Test
    void withdrawUser_invalidUser() {
        long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "test@example.com", UserRole.ROLE_USER);
        UserWithdrawRequest request = new UserWithdrawRequest("password");
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                userService.withdrawUser(authUser, request));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void changePassword_success() {
        long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "test@example.com", UserRole.ROLE_USER);
        UserChangePasswordRequest request = new UserChangePasswordRequest();

        // 필드 주입
        ReflectionTestUtils.setField(request, "oldPassword", "oldPassword");
        ReflectionTestUtils.setField(request, "newPassword", "newPassword1");

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "password", "encodedOldPassword1");

        // Mocking
        given(userRepository.findById(userId)).willReturn(Optional.of(user));
        given(passwordEncoder.matches("oldPassword", user.getPassword())).willReturn(true);
        given(passwordEncoder.matches("newPassword1", user.getPassword())).willReturn(false); // newPassword1과 현재 비밀번호를 비교
        given(passwordEncoder.encode("newPassword1")).willReturn("encodedNewPassword");

        // 비밀번호 변경 실행
        userService.changePassword(authUser, request);

        // 비밀번호가 업데이트 되었는지 확인
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

