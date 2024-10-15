package com.sparta.sal.domain.card.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.card.dto.request.ModifyCardRequest;
import com.sparta.sal.domain.card.dto.request.SaveCardRequest;
import com.sparta.sal.domain.card.dto.response.GetCardResponse;
import com.sparta.sal.domain.card.dto.response.ModifyCardResponse;
import com.sparta.sal.domain.card.dto.response.SaveCardResponse;
import com.sparta.sal.domain.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/card")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<SaveCardResponse> saveCard(@RequestBody SaveCardRequest reqDto){
        return ResponseEntity.ok().body(cardService.saveCard(reqDto));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<GetCardResponse> getCard(@PathVariable Long cardId){
        return ResponseEntity.ok().body(cardService.getCard(cardId));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<ModifyCardResponse> modifyCard(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long cardId, @RequestBody ModifyCardRequest reqDto){
        return ResponseEntity.ok().body(cardService.modifyCard(authUser, cardId, reqDto));
    }

    @DeleteMapping("/{cardId}")
    public void deleteCard(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long cardId){
        cardService.deleteCard(authUser, cardId);
    }


}
