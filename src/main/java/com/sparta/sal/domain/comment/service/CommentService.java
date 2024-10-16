package com.sparta.sal.domain.comment.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.card.entiry.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.comment.dto.request.ModifyCommentRequest;
import com.sparta.sal.domain.comment.dto.request.SaveCommentRequest;
import com.sparta.sal.domain.comment.dto.response.GetCommentListResponse;
import com.sparta.sal.domain.comment.dto.response.ModifyCommentResponse;
import com.sparta.sal.domain.comment.dto.response.SaveCommentResponse;
import com.sparta.sal.domain.comment.entity.Comment;
import com.sparta.sal.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final CardRepository cardRepository;


    @Transactional
    public SaveCommentResponse saveComment(SaveCommentRequest reqDto, Long cardId) {
        Comment comment = new Comment(reqDto);

        Card card =cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        comment.setCard(card);

        Comment savedComment = commentRepository.save(comment);
        return new SaveCommentResponse(savedComment);
    }

    public List<GetCommentListResponse> getCommentList(Long cardId) {
        List<Comment> commentList = commentRepository.findByCard_IdAndIsDeletedOrderByCreatedAtAsc(cardId, false);
        return commentList.stream().map(GetCommentListResponse::new).toList();
    }

    @Transactional
    public ModifyCommentResponse modifyComment(Long cardId, Long commentId, ModifyCommentRequest reqDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new InvalidRequestException("댓글을 찾을 수 없습니다."));

        if(comment.isDeleted()){
            throw new InvalidRequestException("삭제된 댓글입니다.");
        }

        comment.ModifyComment(reqDto);
        return new ModifyCommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(AuthUser authUser, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new InvalidRequestException("card not found"));
        comment.deleteComment();
        commentRepository.save(comment);
    }

}
