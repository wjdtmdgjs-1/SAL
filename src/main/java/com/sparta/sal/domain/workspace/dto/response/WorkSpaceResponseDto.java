package com.sparta.sal.domain.workspace.dto.response;

import lombok.Getter;

@Getter
public class WorkSpaceResponseDto {
    private final Long workSpaceId;

    public WorkSpaceResponseDto(Long workSpaceId) {
        this.workSpaceId = workSpaceId;
    }
}
