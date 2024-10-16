package com.sparta.sal.domain.list.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.board.repository.BoardRepository;
import com.sparta.sal.domain.list.dto.request.ListRequestDto;
import com.sparta.sal.domain.list.dto.request.ListUpdateRequestDto;
import com.sparta.sal.domain.list.dto.response.ListResponseDto;
import com.sparta.sal.domain.list.entity.List;
import com.sparta.sal.domain.list.repository.ListRepository;
import com.sparta.sal.domain.member.enums.MemberRole;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListService {

    private final ListRepository listRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public java.util.List<ListResponseDto> getLists(Long boardId) {
        Board board = findBoardById(boardId);

        return listRepository.findAllByBoard(board)
                .stream()
                .map(ListResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ListResponseDto getList(Long boardId, Long listId) {
        Board board = findBoardById(boardId);
        List list = findListById(listId);
        return ListResponseDto.of(list);
    }

    @Transactional
    public ListResponseDto createList(Long boardId, ListRequestDto listRequestDto, AuthUser authUser) {
        checkIfReadOnly(authUser);
        Board board = findBoardById(boardId);

        // 최대 sequence 값에 1을 더해 새로운 리스트의 sequence로 설정
        int maxSequence = listRepository.findMaxSequenceByBoardId(board.getId()) + 1;

        List list = new List(listRequestDto.getTitle(), maxSequence, board);
        board.addList(list);

        return ListResponseDto.of(listRepository.save(list));
    }

    @Transactional
    public ListResponseDto updateList(Long boardId, Long listId, ListUpdateRequestDto listRequestDto, AuthUser authUser) {
        checkIfReadOnly(authUser);

        Board board = findBoardById(boardId);
        List list = findListById(listId);

        int newSequence = listRequestDto.getSequence();
        int oldSequence = list.getSequence();

        // 리스트의 순서가 변경되었는지 확인 후 순서 업데이트
        if (newSequence != oldSequence) {
            updateListSequences(board, oldSequence, newSequence, false);
        }

        list.setTitle(listRequestDto.getTitle());
        list.setSequence(newSequence);

        return ListResponseDto.of(listRepository.save(list));
    }

    @Transactional
    public void deleteList(Long boardId, Long listId, AuthUser authUser) {
        checkIfReadOnly(authUser);

        Board board = findBoardById(boardId);
        List list = findListById(listId);

        int sequence = list.getSequence();

        board.removeList(list);
        listRepository.delete(list);

        // 삭제된 리스트 이후의 리스트들의 순서를 업데이트 (삭제 플래그 true 전달)
        updateListSequences(board, sequence, Integer.MAX_VALUE, true);
    }

    private void checkIfReadOnly(AuthUser authUser) {
        boolean isReadOnly = authUser.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(MemberRole.READ_ONLY.name()));

        if (isReadOnly) {
            throw new IllegalArgumentException("읽기 전용 역할로는 리스트를 생성, 수정 또는 삭제할 수 없습니다.");
        }
    }

    private Board findBoardById(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("존재하는 보드가 아닙니다."));
    }

    private List findListById(Long listId) {
        return listRepository.findById(listId)
                .orElseThrow(() -> new EntityNotFoundException("존재하는 리스트가 아닙니다."));
    }

    private void updateListSequences(Board board, int oldSequence, int newSequence, boolean isDelete) {
        if (isDelete) {
            // 삭제된 리스트보다 큰 순서의 리스트들만 업데이트
            listRepository.updateSequenceDown(board.getId(), oldSequence, Integer.MAX_VALUE);
        } else {
            // 새로운 순서가 더 클 경우 기존 리스트들의 순서를 앞으로 당김
            if (newSequence > oldSequence) {
                listRepository.updateSequenceDown(board.getId(), oldSequence, newSequence);
            }
            // 새로운 순서가 더 작을 경우 기존 리스트들의 순서를 뒤로 밀음
            else if (newSequence < oldSequence) {
                listRepository.updateSequenceUp(board.getId(), oldSequence, newSequence);
            }
        }
    }
}
