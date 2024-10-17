package com.sparta.sal.domain.comment;


import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.common.service.AlertService;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.comment.dto.request.ModifyCommentRequest;
import com.sparta.sal.domain.comment.dto.request.SaveCommentRequest;
import com.sparta.sal.domain.comment.dto.response.GetCommentListResponse;
import com.sparta.sal.domain.comment.dto.response.ModifyCommentResponse;
import com.sparta.sal.domain.comment.dto.response.SaveCommentResponse;
import com.sparta.sal.domain.comment.entity.Comment;
import com.sparta.sal.domain.comment.repository.CommentRepository;
import com.sparta.sal.domain.comment.service.CommentService;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.user.repository.UserRepository;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AlertService alertService;

    @Test
    void saveComment_success() {
        Long cardId = 1L;
        Long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_USER);

        SaveCommentRequest reqDto = new SaveCommentRequest();
        ReflectionTestUtils.setField(reqDto, "contents", "Test comment");

        // Card 초기화
        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);

        // Board와 WorkSpace 초기화
        Board board = new Board();
        ReflectionTestUtils.setField(board, "id", 1L);

        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(board, "workSpace", workSpace);  // Board의 WorkSpace 필드 초기화

        com.sparta.sal.domain.list.entity.List list = new com.sparta.sal.domain.list.entity.List();
        ReflectionTestUtils.setField(list, "id", 1L);
        ReflectionTestUtils.setField(list, "board", board);  // List의 Board 필드 초기화
        ReflectionTestUtils.setField(card, "list", list);  // Card의 List 필드 초기화

        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));

        // User 초기화
        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);
        ReflectionTestUtils.setField(user, "slackId", "test");
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // Member 초기화
        Member member = new Member();
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.BOARD);
        given(memberRepository.findByWorkSpace_IdAndUser_Id(any(Long.class), any(Long.class))).willReturn(Optional.of(member));

        // Comment 초기화
        Comment savedComment = new Comment();
        ReflectionTestUtils.setField(savedComment, "id", 1L);
        ReflectionTestUtils.setField(savedComment, "commentContent", "Test comment");
        given(commentRepository.save(any(Comment.class))).willReturn(savedComment);

        ReflectionTestUtils.setField(card, "user", user);
        alertService.sendMessage("slackId", "test");

        // 메서드 호출 및 검증
        SaveCommentResponse response = commentService.saveComment(authUser, reqDto, cardId);

        assertNotNull(response);
        assertEquals(savedComment.getCommentContent(), response.getContents());
    }

    @Test
    void saveComment_cardNotFound() {
        Long cardId = 1L;
        Long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_USER);

        SaveCommentRequest reqDto = new SaveCommentRequest();
        ReflectionTestUtils.setField(reqDto, "contents", "Test comment");

        given(cardRepository.findById(cardId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                commentService.saveComment(authUser, reqDto, cardId));
        assertEquals("카드를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void saveComment_userNotFound() {
        Long cardId = 1L;
        Long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_USER);

        SaveCommentRequest reqDto = new SaveCommentRequest();
        ReflectionTestUtils.setField(reqDto, "contents", "Test comment");

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                commentService.saveComment(authUser, reqDto, cardId));
        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void getCommentList_success() {
        Long cardId = 1L;
        Long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_USER);

        // Card 객체 초기화
        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        // Card의 List와 Board 초기화
        com.sparta.sal.domain.list.entity.List list = new com.sparta.sal.domain.list.entity.List();
        Board board = new Board();
        ReflectionTestUtils.setField(board, "id", 1L);
        ReflectionTestUtils.setField(board, "workSpace", workSpace);
        ReflectionTestUtils.setField(list, "board", board);
        ReflectionTestUtils.setField(card, "list", list);

        // Card Repository Mock 설정
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));

        // Member 객체 초기화
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", 1L);

        // Member Repository Mock 설정
        given(memberRepository.findByWorkSpace_IdAndUser_Id(anyLong(), anyLong())).willReturn(Optional.of(member));

        // Comment 객체 초기화
        Comment comment = new Comment();
        ReflectionTestUtils.setField(comment, "id", 1L);
        ReflectionTestUtils.setField(comment, "commentContent", "Test comment");
        ReflectionTestUtils.setField(comment, "isDeleted", false);

        // Comment Repository Mock 설정
        given(commentRepository.findByCard_IdAndIsDeletedOrderByCreatedAtAsc(cardId, false))
                .willReturn(List.of(comment));

        // getCommentList 호출
        List<GetCommentListResponse> response = commentService.getCommentList(authUser, cardId);

        // 응답 검증
        assertEquals(1, response.size());
        assertEquals("Test comment", response.get(0).getCommentContents());
    }

    @Test
    void getCommentList_cardNotFound() {
        Long cardId = 1L;
        Long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_USER);

        given(cardRepository.findById(cardId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                commentService.getCommentList(authUser, cardId));
        assertEquals("카드를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    void modifyComment_success() {
        Long cardId = 1L;
        Long userId = 1L;
        Long commentId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_USER);

        ModifyCommentRequest reqDto = new ModifyCommentRequest();
        ReflectionTestUtils.setField(reqDto, "contents", "Updated comment");

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);

        com.sparta.sal.domain.list.entity.List list = new com.sparta.sal.domain.list.entity.List();
        Board board = new Board();
        ReflectionTestUtils.setField(board, "id", 1L);
        ReflectionTestUtils.setField(list, "board", board);
        ReflectionTestUtils.setField(card, "list", list);

        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));

        Comment comment = new Comment();
        ReflectionTestUtils.setField(comment, "id", commentId);
        User commentUser = new User();
        ReflectionTestUtils.setField(commentUser, "slackId", "test");
        ReflectionTestUtils.setField(commentUser, "id", userId);
        ReflectionTestUtils.setField(comment, "user", commentUser);
        ReflectionTestUtils.setField(card, "user", commentUser);

        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));
        given(commentRepository.save(comment)).willReturn(comment);
        alertService.sendMessage("slackId", "test");

        ModifyCommentResponse response = commentService.modifyComment(authUser, cardId, commentId, reqDto);

        assertEquals("Updated comment", response.getContents());
    }

    @Test
    void modifyComment_notAuthor() {
        Long cardId = 1L;
        Long userId = 1L;
        Long commentId = 1L;
        AuthUser authUser = AuthUser.from(2L, "a@a.com", UserRole.ROLE_USER);

        ModifyCommentRequest reqDto = new ModifyCommentRequest();
        ReflectionTestUtils.setField(reqDto, "contents", "Updated comment");

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));

        Comment comment = new Comment();
        ReflectionTestUtils.setField(comment, "id", commentId);
        User commentUser = new User();
        ReflectionTestUtils.setField(commentUser, "id", userId);
        ReflectionTestUtils.setField(comment, "user", commentUser);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                commentService.modifyComment(authUser, cardId, commentId, reqDto));
        assertEquals("작성자만 수정 또는 삭제 가능합니다", exception.getMessage());
    }

    @Test
    void modifyComment_commentNotFound() {
        Long cardId = 1L;
        Long userId = 1L;
        Long commentId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_USER);

        ModifyCommentRequest reqDto = new ModifyCommentRequest();
        ReflectionTestUtils.setField(reqDto, "contents", "Updated comment");

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        given(cardRepository.findById(cardId)).willReturn(Optional.of(card));
        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                commentService.modifyComment(authUser, cardId, commentId, reqDto));
        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());
    }
}