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

    @PutMapping("/admin/members")
    @Secured(UserRole.Authority.ADMIN)
    public ResponseEntity<MemberResponseDto> changeWorkSpace(@AuthenticationPrincipal AuthUser authUser, @RequestParam Long memberid,
                                                             @RequestParam Long workspaceid, @RequestBody MemberRequestDto memberRequestDto){
        return ResponseEntity.ok(memberService.changeWorkSpace(authUser,memberid,workspaceid,memberRequestDto));
    }

    @PutMapping("/members")
    public ResponseEntity<MemberResponseDto> changeMemberRole(@AuthenticationPrincipal AuthUser authUser, @RequestParam Long memberid,
                                                              @RequestBody MemberRequestDto memberRequestDto){
        return ResponseEntity.ok(memberService.changeMemberRole(authUser,memberid,memberRequestDto));
    }
}
