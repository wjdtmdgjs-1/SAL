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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lists/{listId}")
public class CardController {
    private final CardService cardService;

    @PostMapping
    public ResponseEntity<SaveCardResponse> saveCard( @AuthenticationPrincipal AuthUser authUser,
                                                      @PathVariable Long listId,
                                                      @RequestBody SaveCardRequest reqDto){
        return ResponseEntity.ok().body(cardService.saveCard(reqDto, listId, authUser));
    }

    @GetMapping("/cards/{cardId}")
    public ResponseEntity<GetCardResponse> getCard(@AuthenticationPrincipal AuthUser authUser,
                                                   @PathVariable Long listId,
                                                   @PathVariable Long cardId){
        return ResponseEntity.ok().body(cardService.getCard(authUser, listId, cardId));
    }

    @PutMapping("/cards/{cardId}")
    public ResponseEntity<ModifyCardResponse> modifyCard(@PathVariable Long listId,
                                                         @AuthenticationPrincipal AuthUser authUser,
                                                         @PathVariable Long cardId,
                                                         @RequestBody ModifyCardRequest reqDto){
        return ResponseEntity.ok().body(cardService.modifyCard(listId, authUser, cardId, reqDto));
    }

    @DeleteMapping("/cards/{cardId}")
    public void deleteCard(@AuthenticationPrincipal AuthUser authUser,
                           @PathVariable Long listId,
                           @PathVariable Long cardId){
        cardService.deleteCard(listId, authUser, cardId);
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<GetCardResponse>> getTopRankedCards(@PathVariable Long listId,
                                                        @RequestParam(defaultValue = "10") int top) {
        List<GetCardResponse> topRankedCards = cardService.getTopRankedCards(listId, top);
        return ResponseEntity.ok(topRankedCards);
    }
}
