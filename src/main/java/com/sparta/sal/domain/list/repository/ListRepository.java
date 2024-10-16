package com.sparta.sal.domain.list.repository;

import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.list.entity.List;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ListRepository extends JpaRepository<List, Long>{
    java.util.List<List> findAllByBoard(Board board);

    // 해당 보드에 있는 리스트들 중 가장 큰 sequence 값 찾기
    @Query("SELECT COALESCE(MAX(l.sequence), 0) FROM List l WHERE l.board.id = :boardId")
    int findMaxSequenceByBoardId(@Param("boardId") Long boardId);

    @Modifying
    @Query("UPDATE List l SET l.sequence = l.sequence + 1 WHERE l.board.id = :boardId AND l.sequence >= :newSequence AND l.sequence < :oldSequence")
    void updateSequenceUp(@Param("boardId") Long boardId, @Param("oldSequence") int oldSequence, @Param("newSequence") int newSequence);

    @Modifying
    @Query("UPDATE List l SET l.sequence = l.sequence - 1 WHERE l.board.id = :boardId AND l.sequence > :oldSequence AND l.sequence <= :newSequence")
    void updateSequenceDown(@Param("boardId") Long boardId, @Param("oldSequence") int oldSequence, @Param("newSequence") int newSequence);
}
