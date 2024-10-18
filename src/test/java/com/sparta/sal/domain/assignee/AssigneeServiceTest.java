package com.sparta.sal.domain.assignee;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.common.service.AlertService;
import com.sparta.sal.domain.assignee.dto.response.GetAssigneeResponse;
import com.sparta.sal.domain.assignee.dto.response.SaveAssigneeResponse;
import com.sparta.sal.domain.assignee.entity.Assignee;
import com.sparta.sal.domain.assignee.repository.AssigneeRepository;
import com.sparta.sal.domain.assignee.service.AssigneeService;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssigneeServiceTest {

    @InjectMocks
    private AssigneeService assigneeService;

    @Mock
    private AssigneeRepository assigneeRepository;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private AlertService alertService;

    @Test
    void saveAssignee_Success() {
        // 객체 선언
        AuthUser authUser = AuthUser.from(1L, "test@example.com", UserRole.ROLE_USER);
        User user = User.from("test@example.com", "password", UserRole.ROLE_USER, "Test User");
        WorkSpace workSpace = new WorkSpace(1L, "Test WorkSpace", "This is a test workspace.");
        ReflectionTestUtils.setField(workSpace, "id", 1L);

        // Board 객체 생성
        Board board = new Board(workSpace, "Test Board", "defaultBackground");
        ReflectionTestUtils.setField(board, "id", 1L);

        // List 객체 생성 및 Board와의 관계 설정
        com.sparta.sal.domain.list.entity.List list = new com.sparta.sal.domain.list.entity.List("Test List", 1, board);
        ReflectionTestUtils.setField(list, "id", 1L);
        board.addList(list);

        // Card 객체 생성 및 List와의 관계 설정
        Card card = new Card("Test Card", "This is a test card.", LocalDateTime.now(), "attachment.txt", user);
        ReflectionTestUtils.setField(card, "list", list);
        ReflectionTestUtils.setField(card, "id", 1L);

        // Member 객체 생성
        Member member = new Member(user, workSpace, MemberRole.BOARD);
        ReflectionTestUtils.setField(member, "id", 1L);

        // Assignee 객체 생성
        Assignee newAssignee = new Assignee(card, member);
        ReflectionTestUtils.setField(newAssignee, "id", 1L);

        // Mocking
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(memberRepository.findByWorkSpace_IdAndUser_Id(anyLong(), anyLong())).thenReturn(Optional.of(member));
        when(assigneeRepository.findByCard_IdAndMember_Id(anyLong(), anyLong())).thenReturn(null);
        when(assigneeRepository.save(any(Assignee.class))).thenReturn(newAssignee);

        // Method call
        SaveAssigneeResponse response = assigneeService.saveAssignee(authUser, 1L);

        // Assertions
        assertNotNull(response);
        assertEquals(card.getId(), response.getCardId()); // Card ID
        assertEquals(member.getId(), response.getMemberId()); // Member ID
        verify(assigneeRepository).save(any(Assignee.class));
    }


    @Test
    void getAssignee_Success() {
        // 객체 선언
        AuthUser authUser = AuthUser.from(1L, "test@example.com", UserRole.ROLE_USER);
        User user = User.from("test@example.com", "password", UserRole.ROLE_USER, "Test User");
        WorkSpace workSpace = new WorkSpace(1L, "Test WorkSpace", "This is a test workspace.");
        Board board = new Board(workSpace, "Test Board", "defaultBackground");
        Card card = new Card("Test Card", "This is a test card.", LocalDateTime.now(), "attachment.txt", user);
        Member member = new Member(user, workSpace, MemberRole.BOARD); // MemberRole 수정
        Assignee assignee = new Assignee(card, member);
        ReflectionTestUtils.setField(assignee, "id", 1L); // Assignee ID 설정

        // Mocking
        when(assigneeRepository.findByCard_Id(1L)).thenReturn(List.of(assignee));

        // Method call
        List<GetAssigneeResponse> response = assigneeService.getAssignee(authUser, 1L);

        // Assertions
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(assignee.getId(), response.get(0).getAssigneeId());
        assertEquals(card.getId(), response.get(0).getCardId());
        assertEquals(member.getId(), response.get(0).getMemberId());
    }

    @Test
    void deleteAssignee_Success() {
        // 객체 선언
        User user = User.from("test@example.com", "password", UserRole.ROLE_USER, "Test User");
        WorkSpace workSpace = new WorkSpace(1L, "Test WorkSpace", "This is a test workspace.");
        Member member = new Member(user, workSpace, MemberRole.BOARD);
        Card card = new Card("Test Card", "This is a test card.", LocalDateTime.now(), "attachment.txt", user);
        Assignee assignee = new Assignee(card, member);
        ReflectionTestUtils.setField(assignee, "id", 1L); // Assignee ID 설정

        // Mocking
        when(assigneeRepository.findById(1L)).thenReturn(Optional.of(assignee));

        // Method call
        assigneeService.deleteAssignee(1L);

        // Verification
        verify(assigneeRepository).delete(assignee);
    }

    @Test
    void deleteAssignee_NotFound() {
        // Mocking
        when(assigneeRepository.findById(1L)).thenReturn(Optional.empty());

        // Exception assert
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> {
            assigneeService.deleteAssignee(1L);
        });

        assertEquals("담당자를 찾을 수 없습니다.", exception.getMessage());
    }
}
