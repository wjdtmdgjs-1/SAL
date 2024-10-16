package com.sparta.sal.domain.board.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.board.dto.request.BoardRequestDto;
import com.sparta.sal.domain.board.dto.response.BoardResponseDto;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.board.repository.BoardRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import com.sparta.sal.domain.workspace.repository.WorkSpaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private WorkSpaceRepository workSpaceRepository;

    @InjectMocks
    private BoardService boardService;

    @Test
    public void postBoard_멤버찾기에러() {
        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        BoardRequestDto boardRequestDto = new BoardRequestDto();

        NullPointerException exception = assertThrows(NullPointerException.class,()->boardService.postBoard(authUser,1L,boardRequestDto));

        assertEquals("no such member",exception.getMessage());
    }
    @Test
    public void postBoard_checkReadOnly_Fail(){
        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        WorkSpace workSpace = new WorkSpace();
        User user = new User();
        Member member = new Member(user,workSpace, MemberRole.READ_ONLY);
        given(memberRepository.findMemberWithUserIdAndWorkSpaceId(anyLong(),anyLong())).willReturn(Optional.of(member));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,()->boardService.postBoard(authUser,1L,boardRequestDto));

        assertEquals("you can readOnly",exception.getMessage());
    }
    @Test
    public void postBoard_workspace_찾기실패(){
        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        WorkSpace workSpace = new WorkSpace();
        User user = new User();
        Member member = new Member(user,workSpace, MemberRole.BOARD);

        given(memberRepository.findMemberWithUserIdAndWorkSpaceId(anyLong(),anyLong())).willReturn(Optional.of(member));
        given(workSpaceRepository.findById(anyLong())).willReturn(Optional.empty());

        NullPointerException exception = assertThrows(NullPointerException.class,()->boardService.postBoard(authUser,1L,boardRequestDto));

        assertEquals("no such workspace",exception.getMessage());
    }
    @Test
    public void postBoard성공(){
        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        WorkSpace workSpace = new WorkSpace();
        User user = new User();
        Member member = new Member(user,workSpace, MemberRole.BOARD);

        given(memberRepository.findMemberWithUserIdAndWorkSpaceId(anyLong(),anyLong())).willReturn(Optional.of(member));
        given(workSpaceRepository.findById(anyLong())).willReturn(Optional.of(workSpace));

        BoardResponseDto dto = boardService.postBoard(authUser,1L,boardRequestDto);

        assertNotNull(dto);
    }

    @Test
    public void updateBoard_findboardfail(){
        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        WorkSpace workSpace = new WorkSpace(1L,"title","explain");
        ReflectionTestUtils.setField(workSpace,"id",1L);
        User user = User.from("a@a.com","password", UserRole.valueOf(UserRole.Authority.ADMIN));
        ReflectionTestUtils.setField(user,"id",1L);
        Member member = new Member(user,workSpace, MemberRole.BOARD);
        Board board = new Board(workSpace,"title","background");

        given(memberRepository.findMemberWithUserIdAndWorkSpaceId(anyLong(),anyLong())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyLong())).willReturn(Optional.empty());

        NullPointerException exception = assertThrows(NullPointerException.class,()->boardService.updateBoard(authUser,1L,1L,boardRequestDto));

        assertEquals("no such board",exception.getMessage());
    }
    @Test
    public void updateBoard_boardCheckFail(){
        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        WorkSpace workSpace = new WorkSpace(1L,"title","explain");
        ReflectionTestUtils.setField(workSpace,"id",1L);
        User user = User.from("a@a.com","password", UserRole.valueOf(UserRole.Authority.ADMIN));
        ReflectionTestUtils.setField(user,"id",1L);
        Member member = new Member(user,workSpace, MemberRole.BOARD);
        Board board = new Board(workSpace,"title","background");

        given(memberRepository.findMemberWithUserIdAndWorkSpaceId(anyLong(),anyLong())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));

        InvalidRequestException exception = assertThrows(InvalidRequestException.class,()->boardService.updateBoard(authUser,2L,1L,boardRequestDto));

        assertEquals("that board is not belongs to your workspace",exception.getMessage());
    }
    @Test
    public void updateBoard_성공(){
        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        BoardRequestDto boardRequestDto = new BoardRequestDto();
        ReflectionTestUtils.setField(boardRequestDto,"boardTitle","title");
        ReflectionTestUtils.setField(boardRequestDto,"background","background");
        WorkSpace workSpace = new WorkSpace(1L,"title","explain");
        ReflectionTestUtils.setField(workSpace,"id",1L);
        User user = User.from("a@a.com","password", UserRole.valueOf(UserRole.Authority.ADMIN));
        ReflectionTestUtils.setField(user,"id",1L);
        Member member = new Member(user,workSpace, MemberRole.BOARD);
        Board board = new Board(workSpace,"title","background");

        given(memberRepository.findMemberWithUserIdAndWorkSpaceId(anyLong(),anyLong())).willReturn(Optional.of(member));
        given(boardRepository.findById(anyLong())).willReturn(Optional.of(board));

        BoardResponseDto dto = boardService.updateBoard(authUser,1L,1L,boardRequestDto);

        assertNotNull(dto);
    }

}
