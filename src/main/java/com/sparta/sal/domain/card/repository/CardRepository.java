package com.sparta.sal.domain.card.repository;

import com.sparta.sal.domain.card.entity.Card;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>, CardQueryRepository{
    @Query("SELECT c.id FROM Card c WHERE c.list=:list")
    List<Long> findAllByList(@Param("list")com.sparta.sal.domain.list.entity.List list);

    @Modifying
    @Query("UPDATE Card c " +
            "SET c.cardTitle = CASE WHEN :title IS NOT NULL THEN :title ELSE c.cardTitle END, " +
            "c.cardExplain = CASE WHEN :cardExplain IS NOT NULL THEN :cardExplain ELSE c.cardExplain END, " +
            "c.deadline = CASE WHEN :deadline IS NOT NULL THEN :deadline ELSE c.deadline END, " +
            "c.attachment = CASE WHEN :attachment IS NOT NULL THEN :attachment ELSE c.attachment END " +
            "WHERE c.id = :cardId " +
            "AND c.isDeleted = false")
    int updateCardById(@Param("cardId") Long cardId,
                       @Param("title") String title,
                       @Param("cardExplain") String cardExplain,
                       @Param("deadline") LocalDateTime deadline,
                       @Param("attachment") String attachment);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Card c where c.id = :id")
    Optional<Card> findByIdWithPessimisticLock(Long id);


    Page<Card> searchCards(Long id, String cardTitle, String cardExplain, LocalDate duedate, String deadline, Pageable pageable);
}
