package com.sparta.sal.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardSimpleResponseDto {
    private final Long workSpaceId;
    private final Long boardId;

    public BoardSimpleResponseDto(Long workSpaceId, Long boardId) {

        this.workSpaceId = workSpaceId;
        this.boardId = boardId;
    }
}
