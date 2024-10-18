package com.sparta.sal.domain.workspace.dto.response;

import lombok.Getter;

@Getter
public class WorkSpaceTitleResponseDto {
    private final Long workSpaceId;
    private final Long userId;
    private final String workSpaceTitle;
    private final String explain;

    public WorkSpaceTitleResponseDto(Long workSpaceId, Long userId, String workSpaceTitle, String explain) {
        this.workSpaceId = workSpaceId;
        this.userId = userId;
        this.workSpaceTitle = workSpaceTitle;
        this.explain = explain;
    }
}
