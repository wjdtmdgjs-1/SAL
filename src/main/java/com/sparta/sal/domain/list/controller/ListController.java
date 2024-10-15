package com.sparta.sal.domain.list.controller;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.list.dto.request.ListRequestDto;
import com.sparta.sal.domain.list.dto.response.ListResponseDto;
import com.sparta.sal.domain.list.service.ListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/lists")
public class ListController {

    private final ListService listService;

    @GetMapping("/board/{boardId}")
    public ResponseEntity<List<ListResponseDto>> getLists(@PathVariable Long boardId,
                                                          @AuthenticationPrincipal AuthUser authUser) {
        List<ListResponseDto> responseDtoList = listService.getLists(boardId, authUser);
        return ResponseEntity.ok(responseDtoList);
    }

    @GetMapping("/{listId}")
    public ResponseEntity<ListResponseDto> getList(@PathVariable Long listId,
                                                   @AuthenticationPrincipal AuthUser authUser) {
        ListResponseDto responseDto = listService.getList(listId, authUser);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping
    public ResponseEntity<ListResponseDto> createList(@RequestBody ListRequestDto listRequestDto,
                                                      @AuthenticationPrincipal AuthUser authUser) {
        ListResponseDto responseDto = listService.createList(listRequestDto, authUser);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{listId}")
    public ResponseEntity<ListResponseDto> updateList(@PathVariable Long boardId,
                                                      @PathVariable Long listId,
                                                      @RequestBody ListRequestDto listRequestDto,
                                                      @AuthenticationPrincipal AuthUser authUser) {
        ListResponseDto responseDto = listService.updateList(boardId, listId, listRequestDto, authUser);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<String> deleteList(@PathVariable Long boardId,
                                             @PathVariable Long listId,
                                             @AuthenticationPrincipal AuthUser authUser) {
        listService.deleteList(boardId, listId, authUser);
        return ResponseEntity.ok("리스트가 삭제되었습니다.");
    }
}
