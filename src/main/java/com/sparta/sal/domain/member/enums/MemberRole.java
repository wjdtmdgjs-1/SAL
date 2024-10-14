package com.sparta.sal.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    WORKSPACE,
    BOARD,
    READ_ONLY;
}
