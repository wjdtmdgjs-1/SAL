package com.sparta.sal.domain.workspace.dto.request;

import com.sparta.sal.domain.member.enums.MemberRole;
import lombok.Getter;

@Getter
public class PostMemberRequestDto {
    private Long workSpaceId;
    private Long userId;
    private MemberRole memberRole;

}
