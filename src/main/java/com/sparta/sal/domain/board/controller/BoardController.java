package com.sparta.sal.domain.board.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.board.dto.request.BoardRequestDto;
import com.sparta.sal.domain.board.dto.response.BoardDetailResponseDto;
import com.sparta.sal.domain.board.dto.response.BoardResponseDto;
import com.sparta.sal.domain.board.dto.response.BoardSimpleResponseDto;
import com.sparta.sal.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/workspaces")
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/{workSpaceId}/boards")
    public ResponseEntity<BoardResponseDto> postBoard(@AuthenticationPrincipal AuthUser authUser,
                                                      @PathVariable long workSpaceId,
                                                      @RequestBody BoardRequestDto boardRequestDto){
        return ResponseEntity.ok(boardService.postBoard(authUser,workSpaceId,boardRequestDto));
    }

    @PutMapping("/{workSpaceId}/boards/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@AuthenticationPrincipal AuthUser authUser,
                                                        @PathVariable long workSpaceId,
                                                        @PathVariable long boardId,
                                                        @RequestBody BoardRequestDto boardRequestDto){
        return ResponseEntity.ok(boardService.updateBoard(authUser,workSpaceId,boardId,boardRequestDto));
    }

    @GetMapping("/boards")
    public ResponseEntity<List<BoardSimpleResponseDto>> getBoardList(@AuthenticationPrincipal AuthUser authUser){
        return ResponseEntity.ok(boardService.getBoardList(authUser));
    }

    @GetMapping("/boards/{boardId}")
    public ResponseEntity<List<BoardDetailResponseDto>> getDetailBoardList(@AuthenticationPrincipal AuthUser authUser,
                                                                           @PathVariable long boardId){
        return ResponseEntity.ok(boardService.getDetailBoardList(authUser,boardId));
    }


    @DeleteMapping("/boards/{boardId}")
    public void deleteBoard(@AuthenticationPrincipal AuthUser authUser, @PathVariable long boardId){
        boardService.deleteBoard(authUser,boardId);
    }
}
