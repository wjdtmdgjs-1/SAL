package com.sparta.sal.domain.comment.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SaveCommentRequest {
    String contents;
    String emoji;
}
