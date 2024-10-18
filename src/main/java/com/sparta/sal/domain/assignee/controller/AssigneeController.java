package com.sparta.sal.domain.assignee.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.assignee.dto.response.GetAssigneeResponse;
import com.sparta.sal.domain.assignee.dto.response.SaveAssigneeResponse;
import com.sparta.sal.domain.assignee.service.AssigneeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cards/{cardId}/assignees")
public class AssigneeController {
    private final AssigneeService assigneeService;

    @PostMapping
    public ResponseEntity<SaveAssigneeResponse> saveAssignee(@AuthenticationPrincipal AuthUser authUser, @PathVariable long cardId) {
        return ResponseEntity.ok().body(assigneeService.saveAssignee(authUser, cardId));
    }

    @GetMapping
    public ResponseEntity<List<GetAssigneeResponse>> getAssignee(@AuthenticationPrincipal AuthUser authUser,
                                                                 @PathVariable long cardId) {
        return ResponseEntity.ok().body(assigneeService.getAssignee(authUser, cardId));
    }

    @DeleteMapping("/{assigneeId}")
    public void deleteAssignee(@AuthenticationPrincipal AuthUser authUser,
                               @PathVariable long cardId,
                               @PathVariable long assigneeId) {
        assigneeService.deleteAssignee(assigneeId);

    }

}
