package com.sparta.sal.domain.comment.dto.response;

import com.sparta.sal.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SaveCommentResponse {
    private Long commentId;
    private String contents;
    private String emoji;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public SaveCommentResponse(Comment comment) {
        this.commentId = comment.getId();
        this.contents = comment.getCommentContent();
        this.emoji = comment.getEmoji();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
