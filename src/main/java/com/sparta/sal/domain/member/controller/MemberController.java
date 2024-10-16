package com.sparta.sal.domain.member.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.member.dto.MemberRequestDto;
import com.sparta.sal.domain.member.dto.MemberResponseDto;
import com.sparta.sal.domain.member.service.MemberService;
import com.sparta.sal.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {
    private final MemberService memberService;

    @PutMapping("/admin/{workSpaceId}/members/{memberId}")
    @Secured(UserRole.Authority.ADMIN)
    public ResponseEntity<MemberResponseDto> changeWorkSpace(@AuthenticationPrincipal AuthUser authUser, @PathVariable long memberId,
                                                             @PathVariable long workSpaceId, @RequestBody MemberRequestDto memberRequestDto){
        return ResponseEntity.ok(memberService.changeWorkSpace(authUser,memberId,workSpaceId,memberRequestDto));
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<MemberResponseDto> changeMemberRole(@AuthenticationPrincipal AuthUser authUser, @PathVariable long memberId,
                                                              @RequestBody MemberRequestDto memberRequestDto){
        return ResponseEntity.ok(memberService.changeMemberRole(authUser,memberId,memberRequestDto));
    }
}
