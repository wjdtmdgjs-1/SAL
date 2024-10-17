package com.sparta.sal.domain.card.dto.response;

import com.sparta.sal.domain.card.entity.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class SaveCardResponse {
    private Long id;
    private String title;
    private String cardExplain;
    private LocalDateTime deadline;
    private String attachment;

    public SaveCardResponse(Card card) {
        this.id = card.getId();
        this.title=card.getCardTitle();
        this.cardExplain=card.getCardExplain();
        this.deadline=card.getDeadline();
        this.attachment=card.getAttachment();
    }
}
