package com.sparta.sal.domain.workspace.dto.response;

import com.sparta.sal.domain.member.enums.MemberRole;
import lombok.Getter;

@Getter
public class PostMemberResponseDto {
    private final Long memberId;
    private final Long userId;
    private final Long workspaceId;
    private final MemberRole memberRole;

    public PostMemberResponseDto(Long memberId, Long userId, Long workspaceId, MemberRole memberRole) {
        this.memberId = memberId;
        this.userId = userId;
        this.workspaceId = workspaceId;
        this.memberRole = memberRole;
    }
}
