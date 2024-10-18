package com.sparta.sal.domain.board.repository;

import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board,Long> {
    List<Board> findByWorkSpace(WorkSpace w);
}
