package com.sparta.sal.domain.invite.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.invite.dto.response.InviteAcceptResponseDto;
import com.sparta.sal.domain.invite.dto.request.InviteRequestDto;
import com.sparta.sal.domain.invite.dto.response.InviteResponseDto;
import com.sparta.sal.domain.invite.entity.Invite;
import com.sparta.sal.domain.invite.repository.InviteRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.repository.UserRepository;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import com.sparta.sal.domain.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InviteService {
    private final InviteRepository inviteRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final WorkSpaceRepository workSpaceRepository;

    @Transactional
    public InviteResponseDto inviteWorkSpaceMember(InviteRequestDto inviteRequestDto) {

        User user = userRepository.findById(inviteRequestDto.getUserId()).orElseThrow(()->new NullPointerException("no such user"));
        Invite savedInvite;
        //이미 같은 workspace에서 초대를 보냈는지, 이미 해당 workspace의 멤버인지를 체크한다.
        if(inviteRepository.checkDuplicate(inviteRequestDto.getWorkSpaceId(), user.getId()).isEmpty()
                && memberRepository.checkDuplicate(inviteRequestDto.getWorkSpaceId(),user.getId()).isEmpty()){
            Invite invite = new Invite(user,inviteRequestDto.getMemberRole(), inviteRequestDto.getWorkSpaceId());
            savedInvite = inviteRepository.save(invite);
        }
        else {
            throw new InvalidRequestException("you can't invite that user");
        }
        return new InviteResponseDto(inviteRequestDto.getWorkSpaceId(),
                inviteRequestDto.getMemberRole(),user.getId(),savedInvite.getId());
    }
    @Transactional
    public InviteResponseDto inviteMember(AuthUser authUser, InviteRequestDto inviteRequestDto) {
        Member member = memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(authUser.getId(), MemberRole.WORKSPACE,inviteRequestDto.getWorkSpaceId())
                .orElseThrow(()->new NullPointerException("you are not the workspace of that workspace"));
        User user = userRepository.findById(inviteRequestDto.getUserId()).orElseThrow(()->new NullPointerException("no such user"));
        Invite savedInvite;
        //memberRole WorkSpace 로 초대했는지 체크
        if(inviteRequestDto.getMemberRole().equals(MemberRole.WORKSPACE)){
            throw new InvalidRequestException("you can't invite workspace role");
        }
        //이미 같은 workspace에서 초대를 보냈는지, 이미 해당 workspace의 멤버인지를 체크한다.
        if(inviteRepository.checkDuplicate(member.getWorkSpace().getId(),user.getId()).isEmpty()
                && memberRepository.checkDuplicate(member.getWorkSpace().getId(),user.getId()).isEmpty()){
        Invite invite = new Invite(member,user,inviteRequestDto.getMemberRole(),inviteRequestDto.getWorkSpaceId());
        savedInvite = inviteRepository.save(invite);
        }
        else {
            throw new InvalidRequestException("you can't invite that user");
        }
        return new InviteResponseDto(member.getWorkSpace().getId(),
                inviteRequestDto.getMemberRole(),user.getId(),savedInvite.getId());
    }

    public List<InviteResponseDto> getInviteList(AuthUser authUser) {
        List<Invite> inviteList = inviteRepository.findByUserId(authUser.getId());
        return inviteList.stream().map(Invite -> new InviteResponseDto(Invite.getWorkSpaceId(),
                Invite.getMemberRole(),Invite.getUser().getId(),Invite.getId())).toList();
    }

    @Transactional
    public InviteAcceptResponseDto acceptInvite(AuthUser authUser, long inviteId) {
        Invite invite = inviteRepository.findById(inviteId).orElseThrow(()->new NullPointerException("no such invite"));
        User user = User.fromAuthUser(authUser);
        WorkSpace workSpace = workSpaceRepository.findById(invite.getWorkSpaceId())
                .orElseThrow(()->new NullPointerException("no such workspace"));
        if(!invite.getUser().getId().equals(authUser.getId())){
            throw new InvalidRequestException("you are not the owner of the invite");
        }
        Member member = new Member(user,workSpace,invite.getMemberRole());
        memberRepository.save(member);
        inviteRepository.delete(invite);
        return new InviteAcceptResponseDto(workSpace.getId(),member.getId(),member.getMemberRole());
    }

    @Transactional
    public void refuseInvite(AuthUser authUser, long inviteId) {
        Invite invite = inviteRepository.findById(inviteId).orElseThrow(()->new NullPointerException("no such invite"));
        if(!invite.getUser().getId().equals(authUser.getId())){
            throw new InvalidRequestException("you are not the owner of the invite");
        }
        inviteRepository.delete(invite);
    }
}
