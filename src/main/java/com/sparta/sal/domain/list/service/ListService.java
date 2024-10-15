package com.sparta.sal.domain.list.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.board.repository.BoardRepository;
import com.sparta.sal.domain.list.dto.request.ListRequestDto;
import com.sparta.sal.domain.list.dto.response.ListResponseDto;
import com.sparta.sal.domain.list.entity.List;
import com.sparta.sal.domain.list.repository.ListRepository;
import com.sparta.sal.domain.member.enums.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListService {

    private final ListRepository listRepository;
    private final BoardRepository boardRepository;

    @Transactional(readOnly = true)
    public java.util.List<ListResponseDto> getLists(Long boardId, AuthUser authUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NullPointerException("존재하는 보드가 아닙니다."));

        return listRepository.findAllByBoard(board)
                .stream()
                .map(ListResponseDto::of)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ListResponseDto getList(Long listId, AuthUser authUser) {
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new NullPointerException("존재하는 리스트가 아닙니다."));

        return ListResponseDto.of(list);
    }

    @Transactional
    public ListResponseDto createList(ListRequestDto listRequestDto, AuthUser authUser) {
        checkIfReadOnly(authUser);

        Board board = boardRepository.findById(listRequestDto.getBoardId())
                .orElseThrow(() -> new NullPointerException("존재하는 보드가 아닙니다."));

        List list = new List(listRequestDto.getTitle(), listRequestDto.getSequence(), board);
        board.addList(list);

        return ListResponseDto.of(listRepository.save(list));
    }

    @Transactional
    public ListResponseDto updateList(Long boardId, Long listId, ListRequestDto listRequestDto, AuthUser authUser) {
        checkIfReadOnly(authUser);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NullPointerException("존재하는 보드가 아닙니다."));
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new NullPointerException("존재하는 리스트가 아닙니다."));

        int newSequence = listRequestDto.getSequence();
        int oldSequence = list.getSequence();

        // 리스트의 순서가 변경되었는지 확인
        if (newSequence != oldSequence) {
            updateListSequences(board, oldSequence, newSequence);
        }

        list.setTitle(listRequestDto.getTitle());
        list.setSequence(newSequence);

        return ListResponseDto.of(listRepository.save(list));
    }

    @Transactional
    public void deleteList(Long boardId, Long listId, AuthUser authUser) {
        checkIfReadOnly(authUser);

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NullPointerException("존재하는 보드가 아닙니다."));
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new NullPointerException("존재하는 리스트가 아닙니다."));

        board.removeList(list); // board에서 리스트 제거
        listRepository.delete(list);
    }

    private void checkIfReadOnly(AuthUser authUser) {
        for (GrantedAuthority authority : authUser.getAuthorities()) {
            if (authority.getAuthority().equals(MemberRole.READ_ONLY.name())) {
                throw new IllegalArgumentException("읽기 전용 역할로는 리스트를 생성, 수정 또는 삭제할 수 없습니다.");
            }
        }
    }

    private void updateListSequences(Board board, int oldSequence, int newSequence) {
        // 새로운 순서가 더 클 경우, 기존 리스트들의 순서를 앞으로 당김
        if (newSequence > oldSequence) {
            listRepository.updateSequenceDown(board.getId(), oldSequence, newSequence);
        }
        // 새로운 순서가 더 작을 경우, 기존 리스트들의 순서를 뒤로 밀음
        else if (newSequence < oldSequence) {
            listRepository.updateSequenceUp(board.getId(), oldSequence, newSequence);
        }
    }
}
