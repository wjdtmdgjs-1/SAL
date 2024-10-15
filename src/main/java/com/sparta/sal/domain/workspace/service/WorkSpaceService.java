package com.sparta.sal.domain.workspace.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.member.service.MemberService;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.repository.UserRepository;
import com.sparta.sal.domain.user.service.UserService;
import com.sparta.sal.domain.workspace.dto.request.PostMemberRequestDto;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceFixRequestDto;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceSaveRequestDto;
import com.sparta.sal.domain.workspace.dto.response.PostMemberResponseDto;
import com.sparta.sal.domain.workspace.dto.response.WorkSpaceResponseDto;
import com.sparta.sal.domain.workspace.dto.response.WorkSpaceTitleResponseDto;
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
public class WorkSpaceService {

    private final WorkSpaceRepository workSpaceRepository;
    private final UserService userService;
    private final MemberRepository memberRepository;

    @Transactional
    public WorkSpaceTitleResponseDto saveWorkSpace(AuthUser authUser, WorkSpaceSaveRequestDto workSpaceSaveRequestDto) {
        User user = userService.isValidUser(workSpaceSaveRequestDto.getUserId());
        WorkSpace workSpace = new WorkSpace(authUser.getId(),workSpaceSaveRequestDto.getWorkSpaceTitle(),workSpaceSaveRequestDto.getExplain());
        Member member = new Member(user,workSpace, MemberRole.WORKSPACE);

        workSpaceRepository.save(workSpace);
        memberRepository.save(member);
        workSpace.addMember(member);

        return new WorkSpaceTitleResponseDto(workSpace.getId(), user.getId(), workSpace.getWorkSpaceTitle(), workSpace.getExplains());
    }

    public List<WorkSpaceResponseDto> getWorkSpace(long userid) {
        List<WorkSpace> list = memberRepository.findWorkSpaceIdByUserId(userid);
        List<WorkSpaceResponseDto> dtoList = new ArrayList<>();
        for (WorkSpace w : list){
            WorkSpaceResponseDto dto = new WorkSpaceResponseDto(w.getId());
            dtoList.add(dto);
        }
        return dtoList;
    }

    @Transactional
    public PostMemberResponseDto postMember(AuthUser authUser, PostMemberRequestDto postMemberRequestDto) {
        //로그인한 사람이 해당 workspace에 memberRole workspace인지 확인
        checkWorkSpace(authUser.getId(),postMemberRequestDto.getWorkSpaceId());
        WorkSpace workSpace = findWorkSpace(postMemberRequestDto.getWorkSpaceId());
        User user = userService.isValidUser(postMemberRequestDto.getUserId());
        Member member = new Member(user,workSpace,postMemberRequestDto.getMemberRole());
        memberRepository.save(member);
        //양방향성을 위해 추가
        workSpace.addMember(member);
        return new PostMemberResponseDto(member.getId(),member.getUser().getId(),member.getWorkSpace().getId(),member.getMemberRole());
    }

    @Transactional
    public WorkSpaceTitleResponseDto fixWorkSpace(AuthUser authUser, Long workspaceid, WorkSpaceFixRequestDto workSpaceFixRequestDto) {
        //로그인한 사람이 해당 workspace에 memberRole workspace인지 확인
        checkWorkSpace(authUser.getId(),workspaceid);
        WorkSpace workSpace = findWorkSpace(workspaceid);
        workSpace.update(workSpaceFixRequestDto.getWorkSpaceTitle(),workSpaceFixRequestDto.getExplain());
        return new WorkSpaceTitleResponseDto(workspaceid,authUser.getId(),workSpace.getWorkSpaceTitle(),workSpace.getExplains());
    }
    @Transactional
    public void deleteWorkSpace(AuthUser authUser, Long workspaceid) {
        //로그인한 사람이 해당 workspace에 memberRole workspace인지 확인
        checkWorkSpace(authUser.getId(),workspaceid);
        WorkSpace workSpace = findWorkSpace(workspaceid);
        workSpaceRepository.delete(workSpace);
    }

    public WorkSpace findWorkSpace(long workspaceid){
        return workSpaceRepository.findById(workspaceid).orElseThrow(()->new NullPointerException("no such workspace"));
    }
    public void checkWorkSpace(Long userId,Long workspaceId){
        if(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(userId, MemberRole.WORKSPACE,workspaceId).isEmpty()){
            throw new InvalidRequestException("you are not workspace role");
        }
    }

}
