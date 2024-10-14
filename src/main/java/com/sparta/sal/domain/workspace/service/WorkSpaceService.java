package com.sparta.sal.domain.workspace.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.repository.UserRepository;
import com.sparta.sal.domain.user.service.UserService;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceSaveRequestDto;
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

    public WorkSpace findWorkSpace(long workspaceid){
        return workSpaceRepository.findById(workspaceid).orElseThrow(()->new NullPointerException("no such workspace"));
    }

    @Transactional
    public WorkSpaceTitleResponseDto saveWorkSpace(AuthUser authUser, WorkSpaceSaveRequestDto workSpaceSaveRequestDto) {
        User user = userService.isValidUser(workSpaceSaveRequestDto.getUserId());
        WorkSpace workSpace = new WorkSpace(authUser.getId(),workSpaceSaveRequestDto.getWorkSpaceTitle(),workSpaceSaveRequestDto.getExplain());
        Member member = new Member(user,workSpace, MemberRole.WORKSPACE);

        workSpaceRepository.save(workSpace);
        memberRepository.save(member);

        return new WorkSpaceTitleResponseDto(workSpace.getId(), user.getId(), workSpace.getWorkSpaceTitle(), workSpace.getExplains());
    }

    public List<WorkSpaceResponseDto> getWorkSpace(long userid) {
        List<WorkSpace> list = memberRepository.findWorkSpaceId(userid);
        List<WorkSpaceResponseDto> dtoList = new ArrayList<>();
        for (WorkSpace w : list){
            WorkSpaceResponseDto dto = new WorkSpaceResponseDto(w.getId());
            dtoList.add(dto);
        }
        return dtoList;
    }
}
