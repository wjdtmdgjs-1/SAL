package com.sparta.sal.domain.comment.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.card.dto.request.ModifyCardRequest;
import com.sparta.sal.domain.comment.dto.request.ModifyCommentRequest;
import com.sparta.sal.domain.comment.dto.request.SaveCommentRequest;
import com.sparta.sal.domain.comment.dto.response.GetCommentListResponse;
import com.sparta.sal.domain.comment.dto.response.ModifyCommentResponse;
import com.sparta.sal.domain.comment.dto.response.SaveCommentResponse;
import com.sparta.sal.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/cards/{cardId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<SaveCommentResponse> saveComment(@AuthenticationPrincipal AuthUser authUser,
                                                           @PathVariable Long cardId,
                                                           @RequestBody SaveCommentRequest reqDto){
        return ResponseEntity.ok().body(commentService.saveComment(authUser, reqDto, cardId));
    }

    @GetMapping
    public ResponseEntity<List<GetCommentListResponse>> getComment(@AuthenticationPrincipal AuthUser authUser,
                                                                   @PathVariable Long cardId){
        return ResponseEntity.ok().body(commentService.getCommentList(authUser, cardId));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ModifyCommentResponse> modifyComment(@AuthenticationPrincipal AuthUser authUser,
                                                               @PathVariable Long cardId,
                                                               @PathVariable Long commentId,
                                                               @RequestBody ModifyCommentRequest reqDto){
        return ResponseEntity.ok().body(commentService.modifyComment(authUser, cardId, commentId, reqDto));

    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long cardId, @PathVariable Long commentId){
        commentService.deleteComment(authUser,commentId);

    }
}
