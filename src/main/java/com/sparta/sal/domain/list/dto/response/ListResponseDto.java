package com.sparta.sal.domain.list.dto.response;

import com.sparta.sal.domain.list.entity.List;
import lombok.Getter;

@Getter
public class ListResponseDto {
    private Long id;
    private Long boardId;
    private String title;
    private int sequence;

    public ListResponseDto(Long id, Long boardId, String title, int sequence) {
        this.id = id;
        this.boardId = boardId;
        this.title = title;
        this.sequence = sequence;
    }

    public static ListResponseDto of(List list) {
        return new ListResponseDto(
                list.getId(),
                list.getBoard().getId(),
                list.getTitle(),
                list.getSequence()
        );
    }
}

