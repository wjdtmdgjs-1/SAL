package com.sparta.sal.domain.invite.dto.response;

import com.sparta.sal.domain.member.enums.MemberRole;
import lombok.Getter;

@Getter
public class InviteResponseDto {
    private final Long WorkSpaceId;
    private final MemberRole memberRole;
    private final Long inviteToUserId;
    private final Long inviteId;

    public InviteResponseDto(Long workSpaceId, MemberRole memberRole, Long inviteToUserId, Long inviteId) {
        this.WorkSpaceId = workSpaceId;
        this.memberRole = memberRole;
        this.inviteToUserId = inviteToUserId;
        this.inviteId = inviteId;
    }
}
