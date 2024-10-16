package com.sparta.sal.domain.board.dto.response;

import lombok.Getter;

import java.util.List;

@Getter
public class BoardDetailResponseDto {
    private final Long listId;
    private final java.util.List<Long> cardId;

    public BoardDetailResponseDto(Long listId, List<Long> cardId) {
        this.listId = listId;
        this.cardId = cardId;
    }
}
