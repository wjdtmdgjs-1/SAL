package com.sparta.sal.domain.comment.dto.request;

import lombok.Getter;

@Getter
public class ModifyCommentRequest {
    private String contents;
    private String emoji;
}
