package com.sparta.sal.domain.card.repository;

import com.sparta.sal.domain.card.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
