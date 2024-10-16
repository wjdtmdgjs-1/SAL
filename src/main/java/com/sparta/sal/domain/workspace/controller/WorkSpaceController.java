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

    @GetMapping("/workspaces/{userId}")
    public ResponseEntity<List<WorkSpaceResponseDto>> getWorkSpaceList(@PathVariable long userId){
        return ResponseEntity.ok(workSpaceService.getWorkSpaceList(userId));
    }

    @PutMapping("/workspaces/{workSpaceId}")
    public ResponseEntity<WorkSpaceTitleResponseDto> updateWorkSpace(@AuthenticationPrincipal AuthUser authUser,
                                                                  @PathVariable long workSpaceId,
                                                                  @RequestBody WorkSpaceFixRequestDto workSpaceFixRequestDto){
        return ResponseEntity.ok(workSpaceService.updateWorkSpace(authUser,workSpaceId,workSpaceFixRequestDto));
    }

    @DeleteMapping("/workspaces/{workSpaceId}")
    public void deleteWorkSpace(@AuthenticationPrincipal AuthUser authUser, @PathVariable long workSpaceId){
        workSpaceService.deleteWorkSpace(authUser,workSpaceId);
    }
}
