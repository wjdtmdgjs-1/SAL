package com.sparta.sal.domain.workspace.dto.request;

import lombok.Getter;

@Getter
public class WorkSpaceSaveRequestDto {
    private Long userId;
    private String workSpaceTitle;
    private String explain;
}
