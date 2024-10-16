package com.sparta.sal.domain.assignee.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.assignee.dto.response.GetAssigneeResponse;
import com.sparta.sal.domain.assignee.dto.response.SaveAssigneeResponse;
import com.sparta.sal.domain.assignee.entity.Assignee;
import com.sparta.sal.domain.assignee.repository.AssigneeRepository;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssigneeService {
    private final AssigneeRepository assigneeRepository;
    private final CardRepository cardRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public SaveAssigneeResponse saveAssignee(AuthUser authUser, long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        Member member = checkRole(card, authUser);
        if (member.getMemberRole().equals(MemberRole.READ_ONLY)) {
            throw new InvalidRequestException("권한이 없습니다.");
        }
        Assignee isExist = assigneeRepository.findByCard_IdAndMember_Id(cardId, member.getId());
        if (isExist != null) {
            throw new InvalidRequestException("이미 있는 담당자입니다.");
        }

        Assignee assignee = new Assignee(card, member);
        assigneeRepository.save(assignee);

        return new SaveAssigneeResponse(assignee);

    }

    public List<GetAssigneeResponse> getAssignee(AuthUser authUser, long cardId) {
        List<Assignee> assigneeList = assigneeRepository.findByCard_Id(cardId);
        return assigneeList.stream().map(GetAssigneeResponse::new).toList();
    }

    @Transactional
    public void deleteAssignee(Long assigneeId) {
        Assignee assignee = assigneeRepository.findById(assigneeId)
                .orElseThrow(() -> new InvalidRequestException("담당자를 찾을 수 없습니다."));
        assigneeRepository.delete(assignee);
    }

    private Member checkRole(Card card, AuthUser authUser) {
        Long workSpaceId = card.getList().getBoard().getWorkSpace().getId();
        Member member = memberRepository.findByWorkSpace_IdAndUser_Id(workSpaceId, authUser.getId())
                .orElseThrow(() -> new InvalidRequestException("해당 워크스페이스에 초대되지 않았습니다."));
        return member;
    }


}
