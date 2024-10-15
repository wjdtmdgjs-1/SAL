package com.sparta.sal.domain.board.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.board.dto.request.BoardRequestDto;
import com.sparta.sal.domain.board.dto.response.BoardResponseDto;
import com.sparta.sal.domain.board.dto.response.BoardSimpleResponseDto;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.board.repository.BoardRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.member.service.MemberService;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import com.sparta.sal.domain.workspace.repository.WorkSpaceRepository;
import com.sparta.sal.domain.workspace.service.WorkSpaceService;
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

    public void checkReadOnly(Member member){
        if(member.getMemberRole().equals(MemberRole.READ_ONLY)){
            throw new InvalidRequestException("you can readOnly");
        }
    }
    public Member findMemberWithUserIdAndWorkSpaceId(Long userId,Long workSpaceId){
        return memberRepository.findMemberWithUserIdAndWorkSpaceId(userId,workSpaceId).orElseThrow(()->new NullPointerException("no such member"));
    }

    @Transactional
    public BoardResponseDto postBoard(AuthUser authUser, Long workSpaceId, BoardRequestDto boardRequestDto) {
        Member member = findMemberWithUserIdAndWorkSpaceId(authUser.getId(),workSpaceId);
        checkReadOnly(member);
        WorkSpace workSpace = workSpaceRepository.findById(workSpaceId).orElseThrow(()->new NullPointerException("no such workspace"));
        Board board = new Board(workSpace,boardRequestDto.getBoardTitle(),boardRequestDto.getBackground());
        boardRepository.save(board);
        workSpace.addBoard(board);
        return new BoardResponseDto(workSpaceId, board.getId(), board.getBoardTitle(), board.getBackground());
    }
    @Transactional
    public BoardResponseDto updateBoard(AuthUser authUser, Long workSpaceId, Long boardId, BoardRequestDto boardRequestDto) {
        Member member = findMemberWithUserIdAndWorkSpaceId(authUser.getId(),workSpaceId);
        checkReadOnly(member);
        Board board = boardRepository.findById(boardId).orElseThrow(()->new NullPointerException("no such board"));
        if(!board.getWorkSpace().getId().equals(workSpaceId)){
            throw new InvalidRequestException("that board is not belongs to your workspace");
        }
        board.update(boardRequestDto.getBoardTitle(),boardRequestDto.getBackground());
        return new BoardResponseDto(workSpaceId, board.getId(), board.getBoardTitle(), board.getBackground());
    }

    public List<BoardSimpleResponseDto> getBoard(AuthUser authUser) {
        List<WorkSpace> list = memberRepository.findWorkSpaceIdByUserId(authUser.getId());
        List<Board> boardList = new ArrayList<>();
        for(WorkSpace w : list){
            List<Board> exlist = boardRepository.findByWorkSpace(w);
            boardList.addAll(exlist);
        }
        List<BoardSimpleResponseDto> dtoss = new ArrayList<>();
        for(Board b : boardList){
            BoardSimpleResponseDto dto = new BoardSimpleResponseDto(b.getWorkSpace().getId(),b.getId());
            dtoss.add(dto);
        }
        return dtoss;
    }

    @Transactional
    public void deleteBoard(AuthUser authUser, Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(()->new NullPointerException("no such board"));
        Member member = findMemberWithUserIdAndWorkSpaceId(authUser.getId(),board.getWorkSpace().getId());
        if(member.getMemberRole().equals(MemberRole.READ_ONLY)){
            throw new InvalidRequestException("you can read only");
        }
        boardRepository.delete(board);
    }


}
