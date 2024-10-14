package com.sparta.sal.domain.member.dto;

import com.sparta.sal.domain.member.enums.MemberRole;
import lombok.Getter;

@Getter
public class MemberResponseDto {
    private final Long workSpaceId;
    private final Long userId;
    private final Long memberId;
    private final MemberRole memberRole;

    public MemberResponseDto(Long workSpaceId, Long userId, Long memberId, MemberRole memberRole) {
        this.workSpaceId = workSpaceId;
        this.userId = userId;
        this.memberId = memberId;
        this.memberRole = memberRole;
    }
}
