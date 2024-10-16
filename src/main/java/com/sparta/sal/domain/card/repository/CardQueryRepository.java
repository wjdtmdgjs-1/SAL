package com.sparta.sal.domain.card.repository;

import com.sparta.sal.domain.card.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface CardQueryRepository {
    Page<Card> searchCards(Long boardId, String cardTitle, String cardExplain, LocalDate duedate, String deadline, Pageable pageable);
}
