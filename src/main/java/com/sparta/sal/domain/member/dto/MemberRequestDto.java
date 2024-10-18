package com.sparta.sal.domain.member.dto;

import com.sparta.sal.domain.member.enums.MemberRole;
import lombok.Getter;

@Getter
public class MemberRequestDto {
    private MemberRole memberRole;
}
