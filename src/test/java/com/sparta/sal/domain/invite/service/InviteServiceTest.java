package com.sparta.sal.domain.invite.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.invite.dto.request.InviteRequestDto;
import com.sparta.sal.domain.invite.dto.response.InviteResponseDto;
import com.sparta.sal.domain.invite.entity.Invite;
import com.sparta.sal.domain.invite.repository.InviteRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.enums.UserRole;
import com.sparta.sal.domain.user.repository.UserRepository;
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
public class InviteServiceTest {
    @Mock
    private InviteRepository inviteRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private WorkSpaceRepository workSpaceRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private InviteService inviteService;

    @Test
    public void inviteWorkSpaceMember_userNotFound(){
        InviteRequestDto dto = new InviteRequestDto();
        ReflectionTestUtils.setField(dto,"userId",1L);
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        Exception exception = assertThrows(NullPointerException.class,
                ()-> inviteService.inviteWorkSpaceMember(dto));

        assertEquals("no such user",exception.getMessage());
    }

    @Test
    public void inviteWorkSpaceMember_chekcduplicate_invite(){
        InviteRequestDto dto = new InviteRequestDto();
        ReflectionTestUtils.setField(dto,"userId",1L);
        ReflectionTestUtils.setField(dto,"workSpaceId",1L);

        User user = User.from("a@a.com","password", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user,"id",1L);

        Invite invite = new Invite();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(inviteRepository.checkDuplicate(anyLong(),anyLong())).willReturn(Optional.of(invite));

        Exception exception = assertThrows(InvalidRequestException.class,()-> inviteService.inviteWorkSpaceMember(dto));

        assertEquals("you can't invite that user",exception.getMessage());
    }
    @Test
    public void inviteWorkSpaceMember_chekcduplicate_member(){
        InviteRequestDto dto = new InviteRequestDto();
        ReflectionTestUtils.setField(dto,"userId",1L);
        ReflectionTestUtils.setField(dto,"workSpaceId",1L);

        User user = User.from("a@a.com","password", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user,"id",1L);

        Invite invite = new Invite();
        Member member = new Member();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(inviteRepository.checkDuplicate(anyLong(),anyLong())).willReturn(Optional.empty());
        given(memberRepository.checkDuplicate(anyLong(),anyLong())).willReturn(Optional.of(member));

        Exception exception = assertThrows(InvalidRequestException.class,()-> inviteService.inviteWorkSpaceMember(dto));

        assertEquals("you can't invite that user",exception.getMessage());
    }
    @Test
    public void inviteWorkSpaceMember_success(){
        InviteRequestDto dto = new InviteRequestDto();
        ReflectionTestUtils.setField(dto,"userId",1L);
        ReflectionTestUtils.setField(dto,"workSpaceId",1L);

        User user = User.from("a@a.com","password", UserRole.ROLE_USER);
        ReflectionTestUtils.setField(user,"id",1L);

        Invite invite = new Invite();
        ReflectionTestUtils.setField(invite,"id",1L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(inviteRepository.checkDuplicate(anyLong(),anyLong())).willReturn(Optional.empty());
        given(memberRepository.checkDuplicate(anyLong(),anyLong())).willReturn(Optional.empty());
        given(inviteRepository.save(any())).willReturn(invite);

        InviteResponseDto responseDto = inviteService.inviteWorkSpaceMember(dto);

        assertNotNull(responseDto);
    }

    @Test
    public void inviteMember_checkAuthuser(){
        AuthUser authUser = AuthUser.from(1L,"a@a.com",UserRole.ROLE_USER);
        InviteRequestDto dto = new InviteRequestDto();
        ReflectionTestUtils.setField(dto,"workSpaceId",1L);

        given(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(anyLong(),any(),anyLong())).willReturn(Optional.empty());

        Exception exception = assertThrows(NullPointerException.class,()->inviteService.inviteMember(authUser,dto));

        assertEquals("you are not the workspace of that workspace",exception.getMessage());
    }
    @Test
    public void inviteMember_workspace(){
        AuthUser authUser = AuthUser.from(1L,"a@a.com",UserRole.ROLE_USER);

        InviteRequestDto dto = new InviteRequestDto();
        ReflectionTestUtils.setField(dto,"workSpaceId",1L);
        ReflectionTestUtils.setField(dto,"userId",1L);
        ReflectionTestUtils.setField(dto,"memberRole",MemberRole.WORKSPACE);

        Member member = new Member();
        given(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(anyLong(),any(),anyLong())).willReturn(Optional.of(member));

        User user = new User();
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        Exception exception = assertThrows(InvalidRequestException.class,
                ()->inviteService.inviteMember(authUser,dto));

        assertEquals("you can't invite workspace role",exception.getMessage());
    }

    @Test
    public void inviteMember_success(){
        AuthUser authUser = AuthUser.from(1L,"a@a.com",UserRole.ROLE_USER);

        InviteRequestDto dto = new InviteRequestDto();
        ReflectionTestUtils.setField(dto,"workSpaceId",1L);
        ReflectionTestUtils.setField(dto,"userId",1L);
        ReflectionTestUtils.setField(dto,"memberRole",MemberRole.BOARD);

        WorkSpace workSpace = new WorkSpace();
        ReflectionTestUtils.setField(workSpace,"id",1L);
        Member member = new Member();
        ReflectionTestUtils.setField(member,"workSpace",workSpace);

        given(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(anyLong(),any(),anyLong())).willReturn(Optional.of(member));

        User user = new User();
        ReflectionTestUtils.setField(user,"id",1L);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        Invite invite = new Invite();
        ReflectionTestUtils.setField(invite,"id",1L);

        given(inviteRepository.checkDuplicate(anyLong(),anyLong())).willReturn(Optional.empty());
        given(memberRepository.checkDuplicate(anyLong(),anyLong())).willReturn(Optional.empty());
        given(inviteRepository.save(any())).willReturn(invite);

        InviteResponseDto responseDto = inviteService.inviteMember(authUser,dto);

        assertNotNull(responseDto);
    }
    /*@Test
    public void getInviteList_success(){
        AuthUser authUser = AuthUser.from(1L,"a@a.com",UserRole.ROLE_USER);
        List<Invite> inviteList = new ArrayList<>();

        given(inviteRepository.findByUserId(anyLong())).willReturn(inviteList);

        List<InviteResponseDto> ResponseDto = inviteService.getInviteList(authUser);


    }*/

}
