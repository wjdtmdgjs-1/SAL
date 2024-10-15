package com.sparta.sal.domain.list.dto.request;

import lombok.Getter;

@Getter
public class ListRequestDto {
    private Long boardId;
    private String title;
    private Integer sequence;
}
