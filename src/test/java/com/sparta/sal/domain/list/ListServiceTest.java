package com.sparta.sal.domain.list;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.board.repository.BoardRepository;
import com.sparta.sal.domain.list.dto.request.ListRequestDto;
import com.sparta.sal.domain.list.entity.List;
import com.sparta.sal.domain.list.repository.ListRepository;
import com.sparta.sal.domain.list.service.ListService;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ListServiceTest {

    @Mock
    private ListRepository listRepository;

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private ListService listService;

    @Mock
    private AuthUser authUser;

    @Mock
    private WorkSpace workSpace;

    @Mock
    private Board board;

    @BeforeEach
    void 설정() {
        MockitoAnnotations.openMocks(this);
        when(board.getWorkSpace()).thenReturn(workSpace);
    }

    @Test
    void 리스트_가져오기_멤버일때_성공() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(workSpace.isMember(any(AuthUser.class))).thenReturn(true);
        when(listRepository.findAllByBoard(any(Board.class))).thenReturn(Arrays.asList(
                new List("List 1", 1, board),
                new List("List 2", 2, board)
        ));

        var results = listService.getLists(1L, authUser);

        assertEquals(2, results.size());
        verify(listRepository, times(1)).findAllByBoard(board);
    }

    @Test
    void 리스트_가져오기_멤버아닐때_예외발생() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(workSpace.isMember(any(AuthUser.class))).thenReturn(false);

        InvalidRequestException 예외 = assertThrows(InvalidRequestException.class, () -> {
            listService.getLists(1L, authUser);
        });

        assertEquals("해당 워크스페이스의 멤버가 아닙니다.", 예외.getMessage());
    }

    @Test
    void 리스트_생성_성공() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        when(listRepository.findMaxSequenceByBoardId(anyLong())).thenReturn(1);
        when(listRepository.save(any(List.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ListRequestDto requestDto = new ListRequestDto("새 리스트");
        when(workSpace.isMember(any(AuthUser.class))).thenReturn(true);

        var result = listService.createList(1L, requestDto, authUser);

        assertNotNull(result);
        assertEquals("새 리스트", result.getTitle());
    }

    @Test
    void 리스트_삭제_성공() {
        when(boardRepository.findById(anyLong())).thenReturn(Optional.of(board));
        List 리스트 = new List("삭제할 리스트", 1, board);
        when(listRepository.findById(anyLong())).thenReturn(Optional.of(리스트));

        doNothing().when(listRepository).delete(any(List.class));
        when(workSpace.isMember(any(AuthUser.class))).thenReturn(true);

        listService.deleteList(1L, 1L, authUser);

        verify(listRepository, times(1)).delete(리스트);
    }
}
