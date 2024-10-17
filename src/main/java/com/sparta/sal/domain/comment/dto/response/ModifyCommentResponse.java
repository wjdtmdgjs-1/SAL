package com.sparta.sal.domain.comment.dto.response;

import com.sparta.sal.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ModifyCommentResponse {
    private Long id;
    private String contents;
    private String emoji;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public ModifyCommentResponse(Comment comment) {
        this.id = comment.getId();
        this.contents=comment.getCommentContent();
        this.emoji = comment.getEmoji();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
