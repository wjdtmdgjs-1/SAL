package com.sparta.sal.domain.card.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.card.dto.request.ModifyCardRequest;
import com.sparta.sal.domain.card.dto.response.GetCardResponse;
import com.sparta.sal.domain.card.dto.response.ModifyCardResponse;
import com.sparta.sal.domain.card.dto.response.SaveCardResponse;
import com.sparta.sal.domain.card.service.CardService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lists")
public class CardController {
    private final CardService cardService;

    @PostMapping("/{listId}/cards")
    public ResponseEntity<SaveCardResponse> saveCard(@AuthenticationPrincipal AuthUser authUser,
                                                     @RequestParam("attachment") @Nullable MultipartFile attachment,
                                                     @RequestParam("title") String title,
                                                     @RequestParam("cardExplain") String cardExplain,
                                                     @RequestParam("deadline") LocalDateTime deadline,
                                                     @PathVariable Long listId
    ) {
        return ResponseEntity.ok().body(cardService.saveCard(attachment, title, cardExplain, deadline, listId, authUser));
    }

    @GetMapping("/{listId}/cards/{cardId}")
    public ResponseEntity<GetCardResponse> getCard(@AuthenticationPrincipal AuthUser authUser,
                                                   @PathVariable Long listId,
                                                   @PathVariable Long cardId) {
        return ResponseEntity.ok().body(cardService.getCard(authUser, listId, cardId));
    }

    @PutMapping("/{listId}/cards/{cardId}")
    public ResponseEntity<ModifyCardResponse> modifyCard(@PathVariable Long listId,
                                                         @AuthenticationPrincipal AuthUser authUser,
                                                         @PathVariable Long cardId,
                                                         @RequestBody ModifyCardRequest reqDto) {
        return ResponseEntity.ok().body(cardService.modifyCard(listId, authUser, cardId, reqDto));
    }

    @DeleteMapping("/{listId}/cards/{cardId}")
    public void deleteCard(@AuthenticationPrincipal AuthUser authUser,
                           @PathVariable Long listId,
                           @PathVariable Long cardId) {
        cardService.deleteCard(listId, authUser, cardId);
    }

    @GetMapping("/cards/{cardId}/attachment")
    public ResponseEntity<String> getAttachment(@PathVariable Long cardId) {
        return ResponseEntity.ok().body(cardService.getAttachment(cardId));
    }

    @DeleteMapping("/cards/{cardId}/attachment")
    public void deleteAttachment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long cardId) {
        cardService.deleteAttachment(authUser, cardId);
    }

    @GetMapping("/{listId}/ranking")
    public ResponseEntity<List<GetCardResponse>> getTopRankedCards(@PathVariable Long listId,
                                                        @RequestParam(defaultValue = "10") int top) {
        List<GetCardResponse> topRankedCards = cardService.getTopRankedCards(listId, top);
        return ResponseEntity.ok(topRankedCards);
    }

    @GetMapping("/{listId}/cards/search")
    public ResponseEntity<List<GetCardResponse>> searchCards(
            @PathVariable Long listId,
            @RequestParam(required = false) String cardExplain,
            @RequestParam(required = false) String cardTitle,
            @RequestParam(required = false) LocalDate duedate,
            @RequestParam(required = false) String deadline,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        List<GetCardResponse> cards = cardService.searchCards(
                listId, cardTitle, cardExplain, duedate, deadline, page, size
        );

        return ResponseEntity.ok(cards);
    }

}
