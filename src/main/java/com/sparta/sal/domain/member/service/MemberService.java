package com.sparta.sal.domain.member.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.member.dto.MemberRequestDto;
import com.sparta.sal.domain.member.dto.MemberResponseDto;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import com.sparta.sal.domain.workspace.repository.WorkSpaceRepository;
import com.sparta.sal.domain.workspace.service.WorkSpaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final WorkSpaceRepository workSpaceRepository;

    @Transactional
    public MemberResponseDto changeWorkSpace(AuthUser authUser, Long memberid, Long workspaceid, MemberRequestDto memberRequestDto) {
        Member member = findMember(memberid);
        WorkSpace workSpace = findWorkSpace(workspaceid);
        //admin유저가 해당 workspace를 만든 유저인지 체크
        if(authUser.getId().equals(workSpace.getMakerId())){
            throw new InvalidRequestException("you are not creater of workspace");
        }
        //해당 멤버가 해당 워크스페이스의 멤버인지 체크
        if(!member.getWorkSpace().getId().equals(workspaceid)){
            throw new InvalidRequestException("member not belongs to workspace");
        }
        //원래 workspace인 멤버와 workspace로 새롭게 임명해줄 member에 한해서만 변경 허용
        if(member.getMemberRole().equals(MemberRole.WORKSPACE)){
            member.update(memberRequestDto.getMemberRole());
        } else if (memberRequestDto.getMemberRole().equals(MemberRole.WORKSPACE)) {
            member.update(memberRequestDto.getMemberRole());
        }else {
            throw new InvalidRequestException("not allowed");
        }

        return new MemberResponseDto(workspaceid,member.getUser().getId(),memberid,member.getMemberRole());
    }

    @Transactional
    public MemberResponseDto changeMemberRole(AuthUser authUser, Long memberid, MemberRequestDto memberRequestDto) {
        Member member = findMember(memberid);
        List<Member> workSpaceMember = memberRepository.findByUserIdAndMemberRole(authUser.getId(),MemberRole.WORKSPACE);

        //바꾸려는 memberRole이 workspace인지 확인
        if(member.getMemberRole().equals(MemberRole.WORKSPACE)){
            throw new InvalidRequestException("you can't change workspace");
        }
        if(memberRequestDto.getMemberRole().equals(MemberRole.WORKSPACE)){
            throw new InvalidRequestException("you can't change workspace");
        }

        //바꾸고자 하는 member가 본인이 memberRole.workSpace로 있는 곳 과 같은 workspace인지 확인
        for(Member m: workSpaceMember){
            if (m.getWorkSpace().equals(member.getWorkSpace())) {
                member.update(memberRequestDto.getMemberRole());
                return new MemberResponseDto(member.getWorkSpace().getId(),member.getUser().getId(),memberid,member.getMemberRole());
            }
        }
        throw new InvalidRequestException("the member is not your ");
    }

    public Member findMember(Long memberid){
        return memberRepository.findById(memberid).orElseThrow(()->new NullPointerException("no such member"));
    }

    public WorkSpace findWorkSpace(long workspaceid){
        return workSpaceRepository.findById(workspaceid).orElseThrow(()->new NullPointerException("no such workspace"));
    }
}
