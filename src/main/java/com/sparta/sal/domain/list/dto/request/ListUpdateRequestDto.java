package com.sparta.sal.domain.list.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ListUpdateRequestDto {
    private String title;
    private int sequence;
}