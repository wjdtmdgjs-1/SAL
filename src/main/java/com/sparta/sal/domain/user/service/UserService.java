package com.sparta.sal.domain.user.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.common.service.AlertService;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.dto.request.UserChangePasswordRequest;
import com.sparta.sal.domain.user.dto.request.UserWithdrawRequest;
import com.sparta.sal.domain.user.dto.response.UserResponse;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AlertService alertService;

    /**
     * 유저 단건 조회
     *
     * @param userId : 사용자 ID
     * @return UserResponse : 시용자 ID 와 email 이 담긴 DTO 객체 반환
     */
    public UserResponse getUser(long userId) {
        User user = isValidUser(userId);

        return UserResponse.entityToDto(user);
    }

    /**
     * 회원탈퇴
     *
     * @param authUser : 사용자 ID, email, 권한이 담긴 객체
     */
    @Transactional
    public void withdrawUser(AuthUser authUser, UserWithdrawRequest userWithdrawRequest) {
        User user = isValidUser(authUser.getId());

        if (!passwordEncoder.matches(userWithdrawRequest.getPassword(), user.getPassword())) {
            throw new InvalidRequestException("비밀번호가 틀렸습니다.");
        }

        List<Member> memberList = memberRepository.findAllByUser(user);
        memberRepository.deleteAll(memberList);

        user.withdrawUser();
    }

    /**
     * 비밀번호 변경
     *
     * @param authUser                  : 사용자 ID, email, 권한이 담긴 객체
     * @param userChangePasswordRequest : 기존 비밀번호와 바꿀 비밀번호가 담긴 DTO 객체
     */
    @Transactional
    public void changePassword(AuthUser authUser, UserChangePasswordRequest userChangePasswordRequest) {
        User user = isValidUser(authUser.getId());

        if (passwordEncoder.matches(userChangePasswordRequest.getNewPassword(), user.getPassword())) {
            throw new InvalidRequestException("새 비밀번호는 기존 비밀번호와 같을 수 없습니다.");
        }

        if (!passwordEncoder.matches(userChangePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new InvalidRequestException("잘못된 비밀번호입니다.");
        }

        validateNewPassword(userChangePasswordRequest.getNewPassword());

        String encodedPassword = passwordEncoder.encode(userChangePasswordRequest.getNewPassword());

        user.changePassword(encodedPassword);
    }

    /**
     * 사용자의 Slack ID를 업데이트
     *
     * @param authUser : 사용자 ID, email, 권한이 담긴 객체
     */
    @Transactional
    public void updateSlackId(AuthUser authUser) {
        User user = isValidUser(authUser.getId());

        user.updateSlackID(alertService.findSlackIdByEmail(user.getEmail()));
    }

    /**
     * 유효한 유저인지 검증
     *
     * @param userId : 사용자 ID
     * @return User : 사용자 Entity 객체
     */
    public User isValidUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new InvalidRequestException("User not found"));

        if (user.getUserStatus().equals(false)) {
            throw new InvalidRequestException("탈퇴한 사용자입니다.");
        }

        return user;
    }

    public static void validateNewPassword(String password) {
        if (password.length() < 8 ||
                !password.matches(".*\\d.*") ||
                !password.matches(".*[A-Z].*")) {
            throw new InvalidRequestException("새 비밀번호는 8자 이상이어야 하고, 숫자와 대문자를 포함해야 합니다.");
        }
    }
}
