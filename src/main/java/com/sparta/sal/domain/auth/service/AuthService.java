package com.sparta.sal.domain.auth.service;

import com.sparta.sal.common.config.JwtUtil;
import com.sparta.sal.common.exception.AuthException;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.auth.dto.request.SigninRequest;
import com.sparta.sal.domain.auth.dto.request.SignupRequest;
import com.sparta.sal.domain.auth.dto.response.SigninResponse;
import com.sparta.sal.domain.auth.dto.response.SignupResponse;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 이메일이 중복되는지 확인 후
     * 비밀번호 인코딩 후
     * 권한이 유효한 권한인지 확인 후
     * 유저 생성 후
     * 토큰을 생성하고 쿠키에 담아 반환
     *
     * @param signupRequest : email, 비밀번호, 권한이 담긴 DTO 객체
     * @return signupResponse : 토큰이 들어있는 DTO 객체 반환
     */
    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new InvalidRequestException("이미 존재하는 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());

        UserRole userRole = UserRole.of(signupRequest.getUserRole());

        User newUser = User.from(signupRequest.getEmail(), encodedPassword, userRole);

        User savedUser = userRepository.save(newUser);

        String bearerToken = jwtUtil.createToken(savedUser.getId(), savedUser.getEmail(), userRole);

        jwtUtil.addJwtToCookie(bearerToken);

        return SignupResponse.of(bearerToken);
    }

    /**
     * 유저가 유효한지 확인 후
     * 비밀번호가 일치하는지 확인 후
     * 토큰을 생성하고 쿠키에 담아 반환
     *
     * @param signinRequest : email, 비밀번호가 담긴 DTO 객체
     * @return signinResponse : 토큰이 담긴 DTO 객체 반환
     */
    public SigninResponse signin(SigninRequest signinRequest) {
        User user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(
                () -> new InvalidRequestException("가입되지 않은 유저입니다."));

        if (user.getUserStatus().equals(false)) {
            throw new InvalidRequestException("탈퇴한 사용자입니다.");
        }

        // 로그인 시 이메일과 비밀번호가 일치하지 않을 경우 401을 반환합니다.
        if (!passwordEncoder.matches(signinRequest.getPassword(), user.getPassword())) {
            throw new AuthException("잘못된 비밀번호입니다.");
        }

        String bearerToken = jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole());

        jwtUtil.addJwtToCookie(bearerToken);

        return SigninResponse.of(bearerToken);
    }
}
