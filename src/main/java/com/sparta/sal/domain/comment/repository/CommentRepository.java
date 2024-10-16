package com.sparta.sal.domain.comment.repository;

import com.sparta.sal.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByCard_IdAndIsDeletedOrderByCreatedAtAsc(Long cardId, boolean isDeleted);
}
