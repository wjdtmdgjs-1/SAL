package com.sparta.sal.domain.card.dto.response;

import com.sparta.sal.domain.card.entiry.Card;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GetCardResponse {
    private Long id;
    private String cardTitle;
    private String cardExplain;
    private LocalDateTime deadline;
    private String attachment;

    public GetCardResponse(Card card) {
        this.id = card.getId();
        this.cardTitle = card.getCardTitle();
        this.cardExplain = card.getCardExplain();
        this.deadline = card.getDeadline();
        this.attachment = card.getAttachment();
    }
}
