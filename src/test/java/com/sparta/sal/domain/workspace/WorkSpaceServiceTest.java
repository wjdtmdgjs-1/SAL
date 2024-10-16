package com.sparta.sal.domain.workspace;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.user.service.UserService;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceFixRequestDto;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceSaveRequestDto;
import com.sparta.sal.domain.workspace.dto.response.WorkSpaceTitleResponseDto;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import com.sparta.sal.domain.workspace.repository.WorkSpaceRepository;
import com.sparta.sal.domain.workspace.service.WorkSpaceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class WorkSpaceServiceTest {

    @InjectMocks
    private WorkSpaceService workSpaceService;

    @Mock
    private WorkSpaceRepository workSpaceRepository;

    @Mock
    private UserService userService;

    @Mock
    private MemberRepository memberRepository;

    @Test
    void saveWorkSpace() {
        long userId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_ADMIN);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        WorkSpaceSaveRequestDto workSpaceSaveRequestDto = new WorkSpaceSaveRequestDto();
        ReflectionTestUtils.setField(workSpaceSaveRequestDto, "workSpaceTitle", "Test Workspace");
        ReflectionTestUtils.setField(workSpaceSaveRequestDto, "explain", "Test explanation");
        ReflectionTestUtils.setField(workSpaceSaveRequestDto, "userId", userId);

        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "makerId", 1L);
        ReflectionTestUtils.setField(workSpace, "workSpaceTitle", "Test Workspace");
        ReflectionTestUtils.setField(workSpace, "explains", "Test explanation");

        given(userService.isValidUser(userId)).willReturn(user);
        given(workSpaceRepository.save(any(WorkSpace.class))).willReturn(workSpace);

        // 실행
        WorkSpaceTitleResponseDto response = workSpaceService.saveWorkSpace(authUser, workSpaceSaveRequestDto);

        // 검증
        assertNotNull(response);
        verify(memberRepository).save(any(Member.class)); // 멤버가 저장되었는지 확인
    }

    @Test
    void updateWorkSpace_validUser() {
        long userId = 1L;
        Long workSpaceId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_ADMIN);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "id", workSpaceId); // 워크스페이스 ID 설정
        ReflectionTestUtils.setField(workSpace, "workSpaceTitle", "Original Title");
        ReflectionTestUtils.setField(workSpace, "explains", "Original explanation");

        WorkSpaceFixRequestDto requestDto = new WorkSpaceFixRequestDto();
        ReflectionTestUtils.setField(requestDto, "workSpaceTitle", "Updated Title");
        ReflectionTestUtils.setField(requestDto, "explain", "Updated explanation");

        given(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(userId, MemberRole.WORKSPACE, workSpaceId))
                .willReturn(Optional.of(new Member(user, workSpace, MemberRole.WORKSPACE)));
        given(workSpaceRepository.findById(workSpaceId)).willReturn(Optional.of(workSpace));

        // 실행
        WorkSpaceTitleResponseDto response = workSpaceService.updateWorkSpace(authUser, workSpaceId, requestDto);

        // 검증
        assertNotNull(response);
    }

    @Test
    void updateWorkSpace_invalidUser() {
        long userId = 1L;
        Long workSpaceId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_ADMIN);

        given(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(userId, MemberRole.WORKSPACE, workSpaceId))
                .willReturn(Optional.empty()); // 사용자가 워크스페이스 역할이 없음을 반환

        WorkSpaceFixRequestDto requestDto = new WorkSpaceFixRequestDto();
        ReflectionTestUtils.setField(requestDto, "workSpaceTitle", "Updated Title");
        ReflectionTestUtils.setField(requestDto, "explain", "Updated explanation");

        // 실행 & 검증
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                workSpaceService.updateWorkSpace(authUser, workSpaceId, requestDto));
        assertEquals("you are not workspace role", exception.getMessage());
    }

    @Test
    void deleteWorkSpace_validUser() {
        long userId = 1L;
        Long workSpaceId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_ADMIN);

        User user = new User();
        ReflectionTestUtils.setField(user, "id", userId);

        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace, "id", workSpaceId); // 워크스페이스 ID 설정

        given(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(userId, MemberRole.WORKSPACE, workSpaceId))
                .willReturn(Optional.of(new Member(user, workSpace, MemberRole.WORKSPACE)));
        given(workSpaceRepository.findById(workSpaceId)).willReturn(Optional.of(workSpace));

        // 실행
        workSpaceService.deleteWorkSpace(authUser, workSpaceId);

        // 검증
        verify(workSpaceRepository).delete(workSpace); // 워크스페이스가 삭제되었는지 확인
    }

    @Test
    void deleteWorkSpace_invalidUser() {
        long userId = 1L;
        Long workSpaceId = 1L;
        AuthUser authUser = AuthUser.from(userId, "a@a.com", UserRole.ROLE_ADMIN);

        given(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(userId, MemberRole.WORKSPACE, workSpaceId))
                .willReturn(Optional.empty()); // 사용자가 워크스페이스 역할이 없음을 반환

        // 실행 & 검증
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () ->
                workSpaceService.deleteWorkSpace(authUser, workSpaceId));
        assertEquals("you are not workspace role", exception.getMessage());
    }
}
