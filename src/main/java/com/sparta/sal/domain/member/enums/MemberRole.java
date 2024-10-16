package com.sparta.sal.domain.member.enums;

import com.sparta.sal.common.exception.InvalidRequestException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum MemberRole {

    WORKSPACE(MemberRole.Authority.WORKSPACE),
    BOARD(MemberRole.Authority.BOARD),
    READ_ONLY(MemberRole.Authority.READ_ONLY);

    private final String memberRole;

    public static MemberRole of(String role) {
        return Arrays.stream(MemberRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 MemberRole"));
    }

    public static class Authority {
        public static final String WORKSPACE = "WORKSPACE";
        public static final String BOARD = "BOARD";
        public static final String READ_ONLY = "READ_ONLY";
    }
}
