package com.sparta.sal.domain.invite.dto.request;

import com.sparta.sal.domain.member.enums.MemberRole;
import lombok.Getter;

@Getter
public class InviteRequestDto {
    private Long workSpaceId;
    private Long userId;
    private MemberRole memberRole;

}
