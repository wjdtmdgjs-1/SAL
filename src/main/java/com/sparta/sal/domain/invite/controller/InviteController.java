package com.sparta.sal.domain.invite.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.invite.dto.response.InviteAcceptResponseDto;
import com.sparta.sal.domain.invite.dto.request.InviteRequestDto;
import com.sparta.sal.domain.invite.dto.response.InviteResponseDto;
import com.sparta.sal.domain.invite.service.InviteService;
import com.sparta.sal.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InviteController {
    private final InviteService inviteService;

    @PostMapping("/admin/invites")
    @Secured(UserRole.Authority.ADMIN)
    public ResponseEntity<InviteResponseDto> inviteWorkSpaceMember(@RequestBody InviteRequestDto inviteRequestDto){
        return ResponseEntity.ok(inviteService.inviteWorkSpaceMember(inviteRequestDto));
    }

    @PostMapping("/invites")
    public ResponseEntity<InviteResponseDto> inviteMember(@AuthenticationPrincipal AuthUser authUser,
                                                          @RequestBody InviteRequestDto inviteRequestDto){
        return ResponseEntity.ok(inviteService.inviteMember(authUser,inviteRequestDto));
    }

    @GetMapping("/invites")
    public ResponseEntity<List<InviteResponseDto>> getInviteList(@AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(inviteService.getInviteList(authUser));
    }
    @PostMapping("/invites/{inviteId}/accept")
    public ResponseEntity<InviteAcceptResponseDto> acceptInvite(@AuthenticationPrincipal AuthUser authUser,
                                                                @PathVariable long inviteId){
        return ResponseEntity.ok(inviteService.acceptInvite(authUser,inviteId));
    }
    @DeleteMapping("/invites/{inviteId}/refuse")
    public void refuseInvite(@AuthenticationPrincipal AuthUser authUser,
                             @PathVariable long inviteId){
        inviteService.refuseInvite(authUser,inviteId);
    }
}
