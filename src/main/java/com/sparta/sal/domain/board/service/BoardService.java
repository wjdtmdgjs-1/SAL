package com.sparta.sal.domain.board.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.board.dto.request.BoardRequestDto;
import com.sparta.sal.domain.board.dto.response.BoardDetailResponseDto;
import com.sparta.sal.domain.board.dto.response.BoardResponseDto;
import com.sparta.sal.domain.board.dto.response.BoardSimpleResponseDto;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.board.repository.BoardRepository;
import com.sparta.sal.domain.card.entiry.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.list.repository.ListRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import com.sparta.sal.domain.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final ListRepository listRepository;
    private final CardRepository cardRepository;

    @Transactional
    public BoardResponseDto postBoard(AuthUser authUser, long workSpaceId, BoardRequestDto boardRequestDto) {
        Member member = findMemberWithUserIdAndWorkSpaceId(authUser.getId(), workSpaceId);
        checkReadOnly(member);
        WorkSpace workSpace = workSpaceRepository.findById(workSpaceId).orElseThrow(() -> new NullPointerException("no such workspace"));
        Board board = new Board(workSpace, boardRequestDto.getBoardTitle(), boardRequestDto.getBackground());
        boardRepository.save(board);
        //  workSpace.addBoard(board);
        return new BoardResponseDto(workSpaceId, board.getId(), board.getBoardTitle(), board.getBackground());
    }

    @Transactional
    public BoardResponseDto updateBoard(AuthUser authUser, long workSpaceId, long boardId, BoardRequestDto boardRequestDto) {
        Member member = findMemberWithUserIdAndWorkSpaceId(authUser.getId(), workSpaceId);
        checkReadOnly(member);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("no such board"));
        if (!board.getWorkSpace().getId().equals(workSpaceId)) {
            throw new InvalidRequestException("that board is not belongs to your workspace");
        }
        board.update(boardRequestDto.getBoardTitle(), boardRequestDto.getBackground());
        return new BoardResponseDto(workSpaceId, board.getId(), board.getBoardTitle(), board.getBackground());
    }

    public List<BoardSimpleResponseDto> getBoardList(AuthUser authUser) {
        List<WorkSpace> list = memberRepository.findWorkSpaceIdByUserId(authUser.getId());
        List<Board> boardList = new ArrayList<>();
        for (WorkSpace w : list) {
            List<Board> exlist = boardRepository.findByWorkSpace(w);
            boardList.addAll(exlist);
        }
       /* List<BoardSimpleResponseDto> dtoss = new ArrayList<>();
        for(Board b : boardList){
            BoardSimpleResponseDto dto = new BoardSimpleResponseDto(b.getWorkSpace().getId(),b.getId());
            dtoss.add(dto);
        }*/
        return boardList.stream().map(board -> new BoardSimpleResponseDto(board.getWorkSpace().getId(), board.getId())).toList();
    }

    public List<BoardDetailResponseDto> getDetailBoardList(AuthUser authUser, long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(()->new NullPointerException("no such board"));
        WorkSpace workSpace = board.getWorkSpace();
        //로그인한 사람이 입력한 board의 워크스페이스의 멤버인지 확인
        if(!workSpace.isMember(authUser)){
            throw new InvalidRequestException("board is not belongs to your workspace");
        }
        //board의 모든 리스트를 불러온다
        List<com.sparta.sal.domain.list.entity.List> listList = listRepository.findAllByBoard(board);
        List<BoardDetailResponseDto> dtoList = new ArrayList<>();
        //list안의 카드들의 정보를 꺼내와 저장해준다
        for(com.sparta.sal.domain.list.entity.List l : listList){
            BoardDetailResponseDto dto = new BoardDetailResponseDto(l.getId(),cardRepository.findAllByList(l));
            dtoList.add(dto);
        }
        return dtoList;
    }
    @Transactional
    public void deleteBoard(AuthUser authUser, long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NullPointerException("no such board"));
        Member member = findMemberWithUserIdAndWorkSpaceId(authUser.getId(), board.getWorkSpace().getId());
        if (member.getMemberRole().equals(MemberRole.READ_ONLY)) {
            throw new InvalidRequestException("you can readonly");
        }
        boardRepository.delete(board);
    }

    private void checkReadOnly(Member member) {
        if (member.getMemberRole().equals(MemberRole.READ_ONLY)) {
            throw new InvalidRequestException("you can readOnly");
        }
    }

    private Member findMemberWithUserIdAndWorkSpaceId(long userId, Long workSpaceId) {
        return memberRepository.findMemberWithUserIdAndWorkSpaceId(userId, workSpaceId).orElseThrow(() -> new NullPointerException("no such member"));
    }


}
