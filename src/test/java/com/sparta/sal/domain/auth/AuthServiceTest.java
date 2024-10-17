package com.sparta.sal.domain.auth;

import com.sparta.sal.common.config.JwtUtil;
import com.sparta.sal.common.exception.AuthException;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.common.service.MailService;
import com.sparta.sal.domain.auth.dto.request.ResetPasswordRequest;
import com.sparta.sal.domain.auth.dto.request.SigninRequest;
import com.sparta.sal.domain.auth.dto.request.SignupRequest;
import com.sparta.sal.domain.auth.dto.response.SigninResponse;
import com.sparta.sal.domain.auth.dto.response.SignupResponse;
import com.sparta.sal.domain.auth.service.AuthService;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MailService mailService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Test
    void signup_Success() {
        User testUser = User.from("test@example.com", "encodedPassword!!23", UserRole.ROLE_ADMIN, "Test User");
        ReflectionTestUtils.setField(testUser,"id",1L);
        SignupRequest signupRequest = new SignupRequest("test@example.com", "Password!!123", UserRole.Authority.USER, "Test User");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).thenReturn("bearerToken");

        SignupResponse response = authService.signup(signupRequest);

        assertEquals("bearerToken", response.getBearerToken());
        verify(mailService).sendSlackInviteEmail(testUser.getEmail());
    }

    @Test
    void signup_EmailAlreadyExists() {
        SignupRequest signupRequest = new SignupRequest("test@example.com", "password123",UserRole.Authority.USER, "Test User");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signup(signupRequest);
        });

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    void signin_Success() {
        User testUser = User.from("test@example.com", "encodedPassword", UserRole.ROLE_ADMIN, "Test User");
        ReflectionTestUtils.setField(testUser,"id",1L);

        SigninRequest signinRequest = new SigninRequest("test@example.com", "password123");

        when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(java.util.Optional.of(testUser));
        when(passwordEncoder.matches(signinRequest.getPassword(), testUser.getPassword())).thenReturn(true);
        when(jwtUtil.createToken(anyLong(), anyString(), any(UserRole.class))).thenReturn("bearerToken");

        SigninResponse response = authService.signin(signinRequest);

        assertEquals("bearerToken", response.getBearerToken());
    }

    @Test
    void signin_UserNotFound() {
        SigninRequest signinRequest = new SigninRequest("notfound@example.com", "password123");

        when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(java.util.Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.signin(signinRequest);
        });

        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    void signin_InvalidPassword() {
        User testUser = User.from("test@example.com", "encodedPassword", UserRole.ROLE_ADMIN, "Test User");
        ReflectionTestUtils.setField(testUser,"id",1L);

        SigninRequest signinRequest = new SigninRequest("test@example.com", "wrongPassword");

        when(userRepository.findByEmail(signinRequest.getEmail())).thenReturn(java.util.Optional.of(testUser));
        when(passwordEncoder.matches(signinRequest.getPassword(), testUser.getPassword())).thenReturn(false);

        AuthException exception = assertThrows(AuthException.class, () -> {
            authService.signin(signinRequest);
        });

        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    void findPassword_UserNotFound() {
        String email = "notfound@example.com";

        when(userRepository.existsByEmail(email)).thenReturn(false);

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.findPassword(email);
        });

        assertEquals("입력하신 이메일로 가입된 사용자가 없습니다.", exception.getMessage());
    }

    @Test
    void resetPassword_Success() {
        User testUser = User.from("test@example.com", "encodedPassw@@@113ord", UserRole.ROLE_USER, "Test User");
        ReflectionTestUtils.setField(testUser, "id", 1L); // ID 설정

        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("test@example.com", "token123", "newEncodedPassword123!!!");

        // Mocking behavior
        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(java.util.Optional.of(testUser));

        // Mock ValueOperations
        StringRedisTemplate mockRedisTemplate = mock(StringRedisTemplate.class);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(resetPasswordRequest.getEmail())).thenReturn("token123");
        when(passwordEncoder.encode(resetPasswordRequest.getPassword())).thenReturn("newEncodedPassword123!!!");

        // Call the method under test
        authService.resetPassword(resetPasswordRequest);

        // Validate the password was updated
        assertEquals("newEncodedPassword123!!!", testUser.getPassword());
    }
    @Test
    void resetPassword_InvalidToken() {
        User testUser = User.from("test@example.com", "newEncodedPassword123!!!", UserRole.ROLE_ADMIN, "Test User");
        ReflectionTestUtils.setField(testUser, "id", 1L);

        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest("test@example.com", "invalidToken", "newEncodedPassword123!!!"); // 유효한 비밀번호 사용

        // Mocking behavior
        when(userRepository.findByEmail(resetPasswordRequest.getEmail())).thenReturn(java.util.Optional.of(testUser));

        // Mock ValueOperations
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(resetPasswordRequest.getEmail())).thenReturn("validToken"); // valid token을 반환하도록 설정

        // Expecting an exception
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            authService.resetPassword(resetPasswordRequest);
        });

        assertEquals("인증번호가 유효하지 않습니다.", exception.getMessage());
    }

}
