package com.sparta.sal.domain.user.dto.response;

import com.sparta.sal.domain.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserResponse {

    private final Long userId;
    private final String email;
    private final String name;

    public static UserResponse entityToDto(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getName());
    }
}
