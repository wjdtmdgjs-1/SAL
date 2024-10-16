package com.sparta.sal.domain.workspace.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
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
        //workSpace.addMember(member);

        return new WorkSpaceTitleResponseDto(workSpace.getId(), user.getId(), workSpace.getWorkSpaceTitle(), workSpace.getExplains());
    }

    public List<WorkSpaceResponseDto> getWorkSpaceList(long userid) {
        List<WorkSpace> list = memberRepository.findWorkSpaceIdByUserId(userid);
        /*List<WorkSpaceResponseDto> dtoList = new ArrayList<>();
        for (WorkSpace w : list){
            WorkSpaceResponseDto dto = new WorkSpaceResponseDto(w.getId());
            dtoList.add(dto);
        }*/
        return list.stream().map(WorkSpace -> new WorkSpaceResponseDto(WorkSpace.getId())).toList();
    }

    @Transactional
    public WorkSpaceTitleResponseDto updateWorkSpace(AuthUser authUser, Long workSpaceId, WorkSpaceFixRequestDto workSpaceFixRequestDto) {
        //로그인한 사람이 해당 workspace에 memberRole workspace인지 확인
        checkWorkSpace(authUser.getId(),workSpaceId);
        WorkSpace workSpace = findWorkSpace(workSpaceId);
        workSpace.update(workSpaceFixRequestDto.getWorkSpaceTitle(),workSpaceFixRequestDto.getExplain());
        return new WorkSpaceTitleResponseDto(workSpaceId,authUser.getId(),workSpace.getWorkSpaceTitle(),workSpace.getExplains());
    }

    @Transactional
    public void deleteWorkSpace(AuthUser authUser, long workspaceid) {
        //로그인한 사람이 해당 workspace에 memberRole workspace인지 확인
        checkWorkSpace(authUser.getId(),workspaceid);
        WorkSpace workSpace = findWorkSpace(workspaceid);
        workSpaceRepository.delete(workSpace);
    }

    //workspaceid를 가지고 workspace를 찾는 공통 메서드
    private WorkSpace findWorkSpace(long workspaceid){
        return workSpaceRepository.findById(workspaceid).orElseThrow(()->new NullPointerException("no such workspace"));
    }

    //해당 유저가 특정 workspace에서 memberRole.workspace 인지를 확인하는 공통 메서드
    private void checkWorkSpace(long userId,long workspaceId){
        if(memberRepository.findByUserIdAndMemberRoleAndWorkSpaceId(userId, MemberRole.WORKSPACE,workspaceId).isEmpty()){
            throw new InvalidRequestException("you are not workspace role");
        }
    }

}
