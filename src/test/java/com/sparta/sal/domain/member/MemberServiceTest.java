package com.sparta.sal.domain.member;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.member.dto.MemberRequestDto;
import com.sparta.sal.domain.member.dto.MemberResponseDto;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.member.service.MemberService;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private WorkSpaceRepository workSpaceRepository;
    @InjectMocks
    private MemberService memberService;

    @Test
    public void changeWorkSpace_createrCheck() {
        Long memberId = 1L;
        Long workSpaceId = 1L;
        MemberRequestDto memberRequestDto = new MemberRequestDto();
        Member member = new Member();

        AuthUser authUser = AuthUser.from(2L, "a@a.com", UserRole.ROLE_ADMIN);
        User user = User.from("aa@a.com", "password", UserRole.ROLE_USER, "slackId");
        WorkSpace workSpace = new WorkSpace();

        ReflectionTestUtils.setField(workSpace, "makerId", 1L);
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(member, "workSpace", workSpace);
        ReflectionTestUtils.setField(member, "user", user);
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.WORKSPACE);
        ReflectionTestUtils.setField(memberRequestDto, "memberRole", MemberRole.WORKSPACE);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(workSpaceRepository.findById(anyLong())).willReturn(Optional.of(workSpace));

        Exception exception = assertThrows(InvalidRequestException.class, () -> memberService.changeWorkSpace(authUser, memberId, workSpaceId, memberRequestDto));

        assertEquals("you are not creater of workspace", exception.getMessage());
    }

    @Test
    public void changeWorkSpace_memberCheck() {
        Long memberId = 1L;
        Long workSpaceId = 2L;
        MemberRequestDto memberRequestDto = new MemberRequestDto();
        Member member = new Member();

        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        User user = User.from("aa@a.com", "password", UserRole.ROLE_USER, "slackId");
        WorkSpace workSpace = new WorkSpace();

        ReflectionTestUtils.setField(workSpace, "makerId", 1L);
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(member, "workSpace", workSpace);
        ReflectionTestUtils.setField(member, "user", user);
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.WORKSPACE);
        ReflectionTestUtils.setField(memberRequestDto, "memberRole", MemberRole.WORKSPACE);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(workSpaceRepository.findById(anyLong())).willReturn(Optional.of(workSpace));

        Exception exception = assertThrows(InvalidRequestException.class, () -> memberService.changeWorkSpace(authUser, memberId, workSpaceId, memberRequestDto));

        assertEquals("member not belongs to workspace", exception.getMessage());
    }

    @Test
    public void changeWorkSpace_workspace() {
        Long memberId = 1L;
        Long workSpaceId = 1L;
        MemberRequestDto memberRequestDto = new MemberRequestDto();
        Member member = new Member();

        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        User user = User.from("aa@a.com", "password", UserRole.ROLE_USER, "slackId");
        WorkSpace workSpace = new WorkSpace();

        ReflectionTestUtils.setField(workSpace, "makerId", 1L);
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(member, "workSpace", workSpace);
        ReflectionTestUtils.setField(member, "user", user);
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.BOARD);
        ReflectionTestUtils.setField(memberRequestDto, "memberRole", MemberRole.BOARD);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(workSpaceRepository.findById(anyLong())).willReturn(Optional.of(workSpace));

        Exception exception = assertThrows(InvalidRequestException.class, () -> memberService.changeWorkSpace(authUser, memberId, workSpaceId, memberRequestDto));

        assertEquals("not allowed", exception.getMessage());
    }

    @Test
    public void changeWorkSpace_success() {
        Long memberId = 1L;
        Long workSpaceId = 1L;
        MemberRequestDto memberRequestDto = new MemberRequestDto();
        Member member = new Member();

        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        User user = User.from("aa@a.com", "password", UserRole.ROLE_USER, "slackId");
        WorkSpace workSpace = new WorkSpace();

        ReflectionTestUtils.setField(workSpace, "makerId", 1L);
        ReflectionTestUtils.setField(workSpace, "id", 1L);
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(member, "workSpace", workSpace);
        ReflectionTestUtils.setField(member, "user", user);
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.WORKSPACE);
        ReflectionTestUtils.setField(memberRequestDto, "memberRole", MemberRole.WORKSPACE);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(workSpaceRepository.findById(anyLong())).willReturn(Optional.of(workSpace));

        MemberResponseDto dto = memberService.changeWorkSpace(authUser, memberId, workSpaceId, memberRequestDto);

        assertNotNull(dto);
    }

    @Test
    public void changeMemberRole() {
        User user = User.from("a@a.com", "password", UserRole.ROLE_USER, "slackId");
        ReflectionTestUtils.setField(user, "id", 1L);

        Member member = new Member();
        ReflectionTestUtils.setField(member, "memberRole", MemberRole.BOARD);
        ReflectionTestUtils.setField(member, "user", user);
        long memberId = 1L;
        AuthUser authUser = AuthUser.from(1L, "a@a.com", UserRole.ROLE_ADMIN);
        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(member, "workSpace", workSpace); // 워크스페이스 설정

        MemberRequestDto memberRequestDto = new MemberRequestDto();
        ReflectionTestUtils.setField(memberRequestDto, "memberRole", MemberRole.BOARD); // 변경할 새 역할 설정

        List<Member> memberList = new ArrayList<>();
        Member workspaceMember = new Member();
        ReflectionTestUtils.setField(workspaceMember, "workSpace", workSpace); // 같은 워크스페이스에 속하도록 설정
        ReflectionTestUtils.setField(workspaceMember, "memberRole", MemberRole.WORKSPACE);
        memberList.add(workspaceMember);

        given(memberRepository.findById(anyLong())).willReturn(Optional.of(member));
        given(memberRepository.findByUserIdAndMemberRole(anyLong(), any())).willReturn(memberList);

        MemberResponseDto response = memberService.changeMemberRole(authUser, memberId, memberRequestDto);

        assertNotNull(response);
    }
}
