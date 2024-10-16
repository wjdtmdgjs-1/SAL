package com.sparta.sal.domain.card.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.card.dto.request.ModifyCardRequest;
import com.sparta.sal.domain.card.dto.response.GetCardResponse;
import com.sparta.sal.domain.card.dto.response.ModifyCardResponse;
import com.sparta.sal.domain.card.dto.response.SaveCardResponse;
import com.sparta.sal.domain.card.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lists/{listId}/cards")
public class CardController {
    private final CardService cardService;

    @PostMapping("/{listId}/cards")
    public ResponseEntity<SaveCardResponse> saveCard(@AuthenticationPrincipal AuthUser authUser,
                                                     @RequestParam("attachment") MultipartFile attachment,
                                                     @RequestParam("title") String title,
                                                     @RequestParam("cardExplain") String cardExplain,
                                                     @RequestParam("deadline") LocalDateTime deadline,
                                                     @PathVariable Long listId
    ) {
        return ResponseEntity.ok().body(cardService.saveCard(attachment, title, cardExplain, deadline, listId, authUser));
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<GetCardResponse> getCard(@AuthenticationPrincipal AuthUser authUser,
                                                   @PathVariable Long listId,
                                                   @PathVariable Long cardId) {
        return ResponseEntity.ok().body(cardService.getCard(authUser, listId, cardId));
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<ModifyCardResponse> modifyCard(@PathVariable Long listId,
                                                         @AuthenticationPrincipal AuthUser authUser,
                                                         @PathVariable Long cardId,
                                                         @RequestBody ModifyCardRequest reqDto) {
        return ResponseEntity.ok().body(cardService.modifyCard(listId, authUser, cardId, reqDto));
    }

    @DeleteMapping("/{cardId}")
    public void deleteCard(@AuthenticationPrincipal AuthUser authUser,
                           @PathVariable Long listId,
                           @PathVariable Long cardId) {
        cardService.deleteCard(listId, authUser, cardId);
    }

    // card 첨부 파일 url 조회
    @GetMapping("/cards/{cardId}/attachment")
    public ResponseEntity<String> getAttachment(@PathVariable Long cardId) {
        return ResponseEntity.ok().body(cardService.getAttachment(cardId));
    }

    // card 첨부 파일 삭제
    @DeleteMapping("/cards/{cardId}/attachment")
    public void deleteAttachment(@AuthenticationPrincipal AuthUser authUser, @PathVariable Long cardId) {
        cardService.deleteAttachment(authUser, cardId);
    }
}
