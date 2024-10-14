package com.sparta.sal.domain.card.controller;

import com.sparta.sal.domain.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CardController {
    private final CardService cardService;
}
