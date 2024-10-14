package com.sparta.sal.domain.workspace.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceSaveRequestDto;
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
}
