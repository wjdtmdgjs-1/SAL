package com.sparta.sal.domain.workspace.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.user.repository.UserRepository;
import com.sparta.sal.domain.workspace.dto.request.WorkSpaceSaveRequestDto;
import com.sparta.sal.domain.workspace.dto.response.WorkSpaceTitleResponseDto;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import com.sparta.sal.domain.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkSpaceService {

    private final WorkSpaceRepository workSpaceRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    private User findUser(Long userId){
        return userRepository.findById(userId).orElseThrow(()->new NullPointerException("no such user"));
    }

    @Transactional
    public WorkSpaceTitleResponseDto saveWorkSpace(AuthUser authUser, WorkSpaceSaveRequestDto workSpaceSaveRequestDto) {
        User user = findUser(workSpaceSaveRequestDto.getUserId());
        WorkSpace workSpace = new WorkSpace(authUser.getId(),workSpaceSaveRequestDto.getWorkSpaceTitle(),workSpaceSaveRequestDto.getExplain());
        Member member = new Member(user,workSpace, MemberRole.WORKSPACE);

        workSpaceRepository.save(workSpace);
        memberRepository.save(member);

        return new WorkSpaceTitleResponseDto(workSpace.getId(), user.getId(), workSpace.getWorkSpaceTitle(), workSpace.getExplain());
    }

}
