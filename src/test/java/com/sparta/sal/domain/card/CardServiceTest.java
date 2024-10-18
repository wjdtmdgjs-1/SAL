package com.sparta.sal.domain.card;

import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.card.dto.request.ModifyCardRequest;
import com.sparta.sal.domain.card.dto.response.GetCardResponse;
import com.sparta.sal.domain.card.dto.response.ModifyCardResponse;
import com.sparta.sal.domain.card.dto.response.SaveCardResponse;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.card.service.CardService;
import com.sparta.sal.domain.list.entity.List;
import com.sparta.sal.domain.list.repository.ListRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.user.service.UserService;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.common.service.AlertService;
import com.sparta.sal.common.service.S3Service;
import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.hibernate.jdbc.Work;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.sparta.sal.domain.user.entity.QUser.user;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private ListRepository listRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private AlertService alertService;

    @Mock
    private UserService userService;
    @Mock
    private ValueOperations<String, Object> valueOperations;


    @Test
    void saveCard_ShouldSaveNewCard_WhenValidInput() throws IOException {
        MultipartFile attachment = mock(MultipartFile.class);
        String title = "Test Card";
        String cardExplain = "This is a test card.";
        LocalDateTime deadline = LocalDateTime.now().plusDays(1);
        Long listId = 1L;

        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);

        Board board = new Board();
        WorkSpace workSpace = new WorkSpace();
        User user = new User();
        ReflectionTestUtils.setField(user, "id", 1L);

        List list = new List();
        ReflectionTestUtils.setField(list, "id", listId);
        ReflectionTestUtils.setField(list, "board", board);
        ReflectionTestUtils.setField(board, "workSpace", workSpace);
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(workSpace, "slackChannel", "123");

        Member member = new Member();
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.BOARD);
        ReflectionTestUtils.setField(member, "user", user);
        ReflectionTestUtils.setField(member, "workSpace", workSpace);

        when(listRepository.findById(listId)).thenReturn(Optional.of(list));
        when(userService.isValidUser(authUser.getId())).thenReturn(user);
        when(memberRepository.findByWorkSpace_IdAndUser_Id(anyLong(), anyLong())).thenReturn(Optional.of(member));
        when(s3Service.uploadFile(attachment)).thenReturn("filename");
        when(cardRepository.save(any(Card.class))).thenReturn(new Card(title, cardExplain, deadline, "filename", user));

        SaveCardResponse response = cardService.saveCard(attachment, title, cardExplain, deadline, listId, authUser);

        assertNotNull(response);
        verify(alertService).sendMessage(any(), any());
    }

    @Test
    void getCard_ShouldReturnCard_WhenExists() {
        Long cardId = 1L;
        Long listId = 1L;

        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        Board board = new Board();
        WorkSpace workSpace = new WorkSpace();
        List list = new List();
        ReflectionTestUtils.setField(list, "id", listId);
        ReflectionTestUtils.setField(list, "board", board);
        ReflectionTestUtils.setField(board, "workSpace", workSpace);
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        Member member = new Member();
        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(member, "workSpace", workSpace);

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        ReflectionTestUtils.setField(card, "isDeleted", false);
        ReflectionTestUtils.setField(card, "list", list);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        when(listRepository.findById(listId)).thenReturn(Optional.of(list));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(memberRepository.findByWorkSpace_IdAndUser_Id(anyLong(),anyLong())).thenReturn(Optional.of(member));

        GetCardResponse response = cardService.getCard(authUser, listId, cardId);

        assertNotNull(response);
    }

    @Test
    void getCard_ShouldThrowException_WhenCardIsDeleted() {
        Long cardId = 1L;
        Long listId = 1L;

        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);

        // Board 및 WorkSpace 객체를 생성하고 설정
        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(workSpace, "slackChannel", "123");

        Board board = new Board();
        ReflectionTestUtils.setField(board, "id", 1L);
        ReflectionTestUtils.setField(board, "workSpace", workSpace);

        // List 객체를 생성하고 Board 설정
        List list = new List();
        ReflectionTestUtils.setField(list, "id", listId);
        ReflectionTestUtils.setField(list, "board", board); // 여기서 board를 설정합니다.

        // Card 객체를 생성하고 isDeleted 필드 설정
        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        ReflectionTestUtils.setField(card, "isDeleted", true);

        // Mocking
        when(listRepository.findById(listId)).thenReturn(Optional.of(list));

        // 예외 발생 검증
        assertThrows(InvalidRequestException.class, () -> cardService.getCard(authUser, listId, cardId));
    }

    @Test
    void deleteCard_ShouldRemoveCard_WhenExists() {
        Long cardId = 1L;
        Long listId = 1L;

        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);

        // WorkSpace 객체 생성 및 초기화
        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(workSpace, "slackChannel", "example-channel"); // 필요한 경우 설정

        // Board 객체 생성 및 WorkSpace 설정
        Board board = new Board();
        ReflectionTestUtils.setField(board, "workSpace", workSpace);

        // List 객체 생성 및 ID 및 Board 설정
        List list = new List();
        ReflectionTestUtils.setField(list, "id", listId);
        ReflectionTestUtils.setField(list, "board", board); // List의 Board 설정

        // Card 객체 생성 및 ID 및 isDeleted 설정
        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        ReflectionTestUtils.setField(card, "isDeleted", false); // 초기 상태는 삭제되지 않음
        User user = User.from("a@a.com","password",UserRole.ROLE_USER,"name");

        // Member 객체 생성 및 WorkSpace 및 역할 설정
        Member member = new Member();
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.BOARD);
        ReflectionTestUtils.setField(member, "workSpace", workSpace); // Member의 WorkSpace 설정
        ReflectionTestUtils.setField(member, "user", user);

        // Mocking
        when(listRepository.findById(listId)).thenReturn(Optional.of(list));
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(memberRepository.findByWorkSpace_IdAndUser_Id(anyLong(), anyLong())).thenReturn(Optional.of(member));

        // 카드 삭제 메서드 호출
        cardService.deleteCard(listId, authUser, cardId);

        // 결과 검증
        assertTrue(card.isDeleted()); // 카드가 삭제 상태로 변경되었는지 확인
        verify(cardRepository).save(card); // 카드가 저장되는지 확인 (삭제 상태 반영)
        verify(alertService).sendMessage(any(), any()); // 알림 서비스 호출 검증
    }

    @Test
    void deleteCard_ShouldThrowException_WhenCardIsAlreadyDeleted() {
        Long cardId = 1L;
        Long listId = 1L;

        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);

        // List 객체 생성 및 ID 설정
        List list = new List();
        ReflectionTestUtils.setField(list, "id", listId);

        // Card 객체 생성 및 ID 및 isDeleted 설정
        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        ReflectionTestUtils.setField(card, "isDeleted", true); // 이미 삭제된 카드 설정

        // Mocking
        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));

        // listRepository는 사용되지 않으므로 제거
        // when(listRepository.findById(listId)).thenReturn(Optional.of(list));

        // 예외가 발생하는지 검증
        assertThrows(InvalidRequestException.class, () -> cardService.deleteCard(listId, authUser, cardId));
    }

    @Test
    void getAttachment_ShouldReturnAttachmentUrl_WhenCardExists() {
        Long cardId = 1L;

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        ReflectionTestUtils.setField(card, "attachment", "attachmentKey");

        when(cardRepository.findById(cardId)).thenReturn(Optional.of(card));
        when(s3Service.changeToURL("attachmentKey")).thenReturn("attachmentUrl");

        String url = cardService.getAttachment(cardId);

        assertNotNull(url);
        assertEquals("attachmentUrl", url);
    }

    @Test
    void modifyCard_success() {
        // Arrange
        Long listId = 1L;
        Long cardId = 1L;
        AuthUser authUser = AuthUser.from(1L, "user@example.com", UserRole.ROLE_USER);
        User user = User.from("a@a.com", "aaaaaaaA1!", UserRole.ROLE_USER, "name");

        ModifyCardRequest reqDto = new ModifyCardRequest();
        ReflectionTestUtils.setField(reqDto, "title", "Updated Title");
        ReflectionTestUtils.setField(reqDto, "cardExplain", "Updated cardExplain");

        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(workSpace, "slackChannel", "zz");

        Board board = new Board();
        ReflectionTestUtils.setField(board, "id", 1L);
        ReflectionTestUtils.setField(board, "workSpace", workSpace);

        List list = new List();
        ReflectionTestUtils.setField(list, "id", listId);
        ReflectionTestUtils.setField(list, "board", board);

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        ReflectionTestUtils.setField(card, "cardTitle", "Original Title");
        ReflectionTestUtils.setField(card, "isDeleted", false);
        ReflectionTestUtils.setField(card, "list", list);

        Member member = new Member();
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.BOARD);
        ReflectionTestUtils.setField(member, "workSpace", workSpace);
        ReflectionTestUtils.setField(member, "user", user);

        // Mocking repository behaviors
        given(cardRepository.findByIdWithPessimisticLock(cardId)).willReturn(Optional.of(card));
        given(listRepository.findById(listId)).willReturn(Optional.of(list));
        given(memberRepository.findByWorkSpace_IdAndUser_Id(anyLong(), anyLong())).willReturn(Optional.of(member));
        given(cardRepository.updateCardById(anyLong(), anyString(), anyString(), any(), any())).willReturn(1);

        // Mocking the Slack alert service
        doNothing().when(alertService).sendMessage(anyString(), anyString());

        // Act
        ModifyCardResponse response = cardService.modifyCard(listId, authUser, cardId, reqDto);

        // Assert
        assertEquals("Updated Title", response.getTitle());
        assertEquals("Updated cardExplain", response.getCardExplain());
        verify(alertService).sendMessage(anyString(), contains("님이 카드 Updated Title를 수정하셨습니다."));
    }
    @Test
    void modifyCard_PessimisticLock_WithoutLock() throws InterruptedException {
        // Arrange
        Long listId = 1L;
        Long cardId = 1L;
        AuthUser authUser = AuthUser.from(1L, "user@example.com", UserRole.ROLE_USER);
        User user = User.from("a@a.com", "aaaaaaaA1!", UserRole.ROLE_USER, "name");

        ModifyCardRequest reqDto = new ModifyCardRequest();
        ReflectionTestUtils.setField(reqDto, "title", "Updated Title");
        ReflectionTestUtils.setField(reqDto, "cardExplain", "Updated cardExplain");

        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(workSpace, "slackChannel", "zz");

        Board board = new Board();
        ReflectionTestUtils.setField(board, "id", 1L);
        ReflectionTestUtils.setField(board, "workSpace", workSpace);

        List list = new List();
        ReflectionTestUtils.setField(list, "id", listId);
        ReflectionTestUtils.setField(list, "board", board);

        Card card = new Card();
        ReflectionTestUtils.setField(card, "id", cardId);
        ReflectionTestUtils.setField(card, "cardTitle", "Original Title");
        ReflectionTestUtils.setField(card, "isDeleted", false);
        ReflectionTestUtils.setField(card, "list", list);

        Member member = new Member();
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.BOARD);
        ReflectionTestUtils.setField(member, "workSpace", workSpace);
        ReflectionTestUtils.setField(member, "user", user);

        // Mocking repository behaviors
        given(cardRepository.findByIdWithPessimisticLock(cardId)).willReturn(Optional.of(card));
        given(listRepository.findById(listId)).willReturn(Optional.of(list));
        given(memberRepository.findByWorkSpace_IdAndUser_Id(anyLong(), anyLong())).willReturn(Optional.of(member));
        given(cardRepository.updateCardById(anyLong(), anyString(), anyString(), any(), any())).willReturn(1);

        // Act
        Runnable task = () -> {
            try {
                cardService.modifyCard(listId, authUser, cardId, reqDto);
            } catch (Exception e) {
                // Handle exception if necessary
            }
        };

        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        // 비관적 잠금이 없으므로 모든 스레드가 성공적으로 수정했으면 안 됨
        Card updatedCard = cardRepository.findByIdWithPessimisticLock(cardId)
                .orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        // 확인: 마지막 스레드의 수정 내용이 반영되었는지
        assertEquals("Updated Title", updatedCard.getCardTitle());
        assertEquals("Updated cardExplain", updatedCard.getCardExplain());
    }


}
