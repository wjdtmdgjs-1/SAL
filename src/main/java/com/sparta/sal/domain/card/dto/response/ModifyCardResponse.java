package com.sparta.sal.domain.card.dto.response;

import com.sparta.sal.domain.card.entiry.Card;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ModifyCardResponse {
    private Long id;
    private String title;
    private String cardExplain;
    private LocalDateTime deadline;
    private String attachment;

    public ModifyCardResponse(Card card){
        this.id = card.getId();
        this.title=card.getCardTitle();
        this.cardExplain=card.getCardExplain();
        this.deadline=card.getDeadline();
        this.attachment=card.getAttachment();
    }
}
