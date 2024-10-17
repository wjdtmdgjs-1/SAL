package com.sparta.sal.domain.card.repository;

import com.sparta.sal.domain.card.entity.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long>, CardQueryRepository{
    @Query("SELECT c.id FROM Card c WHERE c.list=:list")
    List<Long> findAllByList(@Param("list")com.sparta.sal.domain.list.entity.List list);

    Page<Card> searchCards(Long id, String cardTitle, String cardExplain, LocalDate duedate, String deadline, Pageable pageable);
}
