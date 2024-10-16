package com.sparta.sal.domain.comment.dto.response;

import com.sparta.sal.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetCommentListResponse {
    private Long commentId;
    private String commentContents;
    private String emoji;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public GetCommentListResponse(Comment comment) {
        this.commentId = comment.getId();
        this.commentContents = comment.getCommentContent();
        this.emoji = comment.getEmoji();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
