package com.sparta.sal.domain.card.repository;

import com.sparta.sal.domain.card.entiry.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("SELECT c.id FROM Card c WHERE c.list=:list")
    List<Long> findAllByList(@Param("list")com.sparta.sal.domain.list.entity.List list);
}
