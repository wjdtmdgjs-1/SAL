package com.sparta.sal.domain.comment.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.common.service.AlertService;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.comment.dto.request.ModifyCommentRequest;
import com.sparta.sal.domain.comment.dto.request.SaveCommentRequest;
import com.sparta.sal.domain.comment.dto.response.GetCommentListResponse;
import com.sparta.sal.domain.comment.dto.response.ModifyCommentResponse;
import com.sparta.sal.domain.comment.dto.response.SaveCommentResponse;
import com.sparta.sal.domain.comment.entity.Comment;
import com.sparta.sal.domain.comment.repository.CommentRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final AlertService alertService;

    @Transactional
    public SaveCommentResponse saveComment(AuthUser authUser, SaveCommentRequest reqDto, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));
        User user = userRepository.findById(authUser.getId()).orElseThrow(() -> new InvalidRequestException("사용자를 찾을 수 없습니다."));

        Member member = checkRole(card, authUser);
        if (member.getMemberRole().equals(MemberRole.READ_ONLY)) {
            throw new InvalidRequestException("권한이 없습니다.");
        }

        Comment comment = new Comment(reqDto);
        comment.addCard(card);
        comment.addUser(user);

        Comment savedComment = commentRepository.save(comment);

        alertService.sendMessage(card.getUser().getSlackId()
                , user.getName() + "님이 카드 " + card.getCardTitle() + "에 댓글을 작성하셨습니다.");

        return new SaveCommentResponse(savedComment);
    }

    public List<GetCommentListResponse> getCommentList(AuthUser authUser, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));
        checkRole(card, authUser);

        List<Comment> commentList = commentRepository.findByCard_IdAndIsDeletedOrderByCreatedAtAsc(cardId, false);
        return commentList.stream().map(GetCommentListResponse::new).toList();
    }

    @Transactional
    public ModifyCommentResponse modifyComment(AuthUser authUser, Long cardId, Long commentId, ModifyCommentRequest reqDto) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new InvalidRequestException("댓글을 찾을 수 없습니다."));

        if (comment.isDeleted()) {
            throw new InvalidRequestException("삭제된 댓글입니다.");
        }

        if (!comment.getUser().getId().equals(authUser.getId())) {
            throw new InvalidRequestException("작성자만 수정 또는 삭제 가능합니다");
        }

        comment.ModifyComment(reqDto);

        alertService.sendMessage(card.getUser().getSlackId()
                , comment.getUser().getName() + "님이 카드 " + card.getCardTitle() + "에 댓글을 수정하셨습니다.");

        return new ModifyCommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public void deleteComment(AuthUser authUser, Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new InvalidRequestException("댓글을 찾을 수 없습니다."));
        if (!comment.getUser().getId().equals(authUser.getId())) {
            throw new InvalidRequestException("작성자만 수정 또는 삭제 가능합니다");
        }

        comment.deleteComment();
        commentRepository.save(comment);

        alertService.sendMessage(comment.getCard().getUser().getSlackId()
                , comment.getUser().getName() + "님이 카드 " + comment.getCard().getCardTitle() + "에 댓글을 삭제하셨습니다.");
    }

    private Member checkRole(Card card, AuthUser authUser) {
        Long workSpaceId = card.getList().getBoard().getWorkSpace().getId();
        Member member = memberRepository.findByWorkSpace_IdAndUser_Id(workSpaceId, authUser.getId())
                .orElseThrow(() -> new InvalidRequestException("해당 워크스페이스에 초대되지 않았습니다."));
        return member;
    }

}
