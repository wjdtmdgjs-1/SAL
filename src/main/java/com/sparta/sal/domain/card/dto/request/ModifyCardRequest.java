package com.sparta.sal.domain.card.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class ModifyCardRequest {
    private String title;
    private String cardExplain;
    private LocalDateTime deadline;
    private String attachment;

}
