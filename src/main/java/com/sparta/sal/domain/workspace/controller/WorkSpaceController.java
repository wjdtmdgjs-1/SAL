package com.sparta.sal.domain.workspace.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.workspace.dto.request.PostMemberRequestDto;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceFixRequestDto;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceSaveRequestDto;
import com.sparta.sal.domain.workspace.dto.response.PostMemberResponseDto;
import com.sparta.sal.domain.workspace.dto.response.WorkSpaceResponseDto;
import com.sparta.sal.domain.workspace.dto.response.WorkSpaceTitleResponseDto;
import com.sparta.sal.domain.workspace.service.WorkSpaceService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class WorkSpaceController {
    private final WorkSpaceService workSpaceService;

    @PostMapping("/admin/workspaces")
    @Secured(UserRole.Authority.ADMIN)
    public ResponseEntity<WorkSpaceTitleResponseDto> saveWorkSpace(@AuthenticationPrincipal AuthUser authUser, @RequestBody WorkSpaceSaveRequestDto workSpaceSaveRequestDto){
            return ResponseEntity.ok(workSpaceService.saveWorkSpace(authUser,workSpaceSaveRequestDto));
    }

    @GetMapping("/workspaces")
    public ResponseEntity<List<WorkSpaceResponseDto>> getWorkSpace(@RequestParam Long userid){
        return ResponseEntity.ok(workSpaceService.getWorkSpace(userid));
    }

    @PostMapping("/workspaces/members")
    public ResponseEntity<PostMemberResponseDto> postMember(@AuthenticationPrincipal AuthUser authUser ,
                                                            @RequestBody PostMemberRequestDto postMemberRequestDto){
        return ResponseEntity.ok(workSpaceService.postMember(authUser,postMemberRequestDto));
    }

    @PutMapping("/workspaces")
    public ResponseEntity<WorkSpaceTitleResponseDto> fixWorkSpace(@AuthenticationPrincipal AuthUser authUser,
                                                                  @RequestParam Long workspaceid,
                                                                  @RequestBody WorkSpaceFixRequestDto workSpaceFixRequestDto){
        return ResponseEntity.ok(workSpaceService.fixWorkSpace(authUser,workspaceid,workSpaceFixRequestDto));
    }

    @DeleteMapping("/workspaces")
    public void deleteWorkSpace(@AuthenticationPrincipal AuthUser authUser, @RequestParam Long workspaceid){
        workSpaceService.deleteWorkSpace(authUser,workspaceid);
    }
}
