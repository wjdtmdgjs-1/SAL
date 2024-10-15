package com.sparta.sal.domain.board.dto.response;

import lombok.Getter;

@Getter
public class BoardResponseDto {
    private final Long workSpaceId;
    private final Long boardId;
    private final String boardTitle;
    private final String background;

    public BoardResponseDto(Long workSpaceId, Long boardId, String boardTitle, String background) {
        this.workSpaceId = workSpaceId;
        this.boardId = boardId;
        this.boardTitle = boardTitle;
        this.background = background;
    }
}
