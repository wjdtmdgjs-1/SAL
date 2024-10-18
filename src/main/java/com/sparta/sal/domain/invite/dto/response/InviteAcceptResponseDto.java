package com.sparta.sal.domain.invite.dto.response;

import com.sparta.sal.domain.member.enums.MemberRole;
import lombok.Getter;

@Getter
public class InviteAcceptResponseDto {
    private final Long workSpaceId;
    private final Long memberId;
    private final MemberRole memberRole;

    public InviteAcceptResponseDto(Long workSpaceId, Long memberId, MemberRole memberRole) {
        this.workSpaceId = workSpaceId;
        this.memberId = memberId;
        this.memberRole = memberRole;
    }
}
