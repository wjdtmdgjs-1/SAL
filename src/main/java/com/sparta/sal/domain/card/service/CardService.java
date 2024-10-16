package com.sparta.sal.domain.card.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.card.dto.request.ModifyCardRequest;
import com.sparta.sal.domain.card.dto.request.SaveCardRequest;
import com.sparta.sal.domain.card.dto.response.GetCardResponse;
import com.sparta.sal.domain.card.dto.response.ModifyCardResponse;
import com.sparta.sal.domain.card.dto.response.SaveCardResponse;
import com.sparta.sal.domain.card.entiry.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.list.entity.List;
import com.sparta.sal.domain.list.repository.ListRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import com.sparta.sal.domain.workspace.repository.WorkSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final WorkSpaceRepository workSpaceRepository;
    private final MemberRepository memberRepository;

    /**
     * 카드 생성
     *
     * @param reqDto : 제목, 설명, 마감일, 첨부파일
     * @return SaveCardResponse : id, 제목, 설명, 마감일, 첨부파일
     */
    @Transactional
    public SaveCardResponse saveCard(SaveCardRequest reqDto, Long listId, AuthUser authUser) {

        List list= listRepository.findById(listId).orElseThrow(() -> new InvalidRequestException("리스트를 찾을 수 없습니다."));
        Member member = checkRole(list, authUser); // 권한 확인


        if(member.getMemberRole().equals(MemberRole.READ_ONLY)){
            throw new InvalidRequestException("권한이 없습니다.");
        }

        Card card = new Card(reqDto);
        card.addList(list);
        Card saveCard = cardRepository.save(card);
        return new SaveCardResponse(saveCard);
    }

    /**
     * 카드 상세 조회
     *
     * @param cardId : card id
     * @return GetCardResponse : id, 제목, 설명, 마감일, 첨부파일
     */
    public GetCardResponse getCard(AuthUser authUser, Long listId, Long cardId) {
        List list= listRepository.findById(listId).orElseThrow(() -> new InvalidRequestException("리스트를 찾을 수 없습니다."));
        checkRole(list, authUser); // 권한 확인
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        if(card.isDeleted()){
            throw new InvalidRequestException("삭제된 카드입니다.");
        }

        return new GetCardResponse(card);
    }

    /**
     * 카드 수정
     *
     * @param authUser : 사용자 ID, email, 권한이 담긴 객체
     * @param cardId : card id
     * @param reqDto : id, 제목, 설명, 마감일, 첨부파일
     * @return ModifyCardResponse : id, 제목, 설명, 마감일, 첨부파일
     */
    @Transactional
    public ModifyCardResponse modifyCard(Long listId, AuthUser authUser, Long cardId, ModifyCardRequest reqDto) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        if(card.isDeleted()){
            throw new InvalidRequestException("삭제된 카드입니다.");
        }

        List list= listRepository.findById(listId).orElseThrow(() -> new InvalidRequestException("리스트를 찾을 수 없습니다."));
        Member member = checkRole(list, authUser); // 권한 확인

        if(member.getMemberRole().equals(MemberRole.READ_ONLY)){
            throw new InvalidRequestException("권한이 없습니다.");
        }

        card.modifyCard(reqDto);
        return new ModifyCardResponse(cardRepository.save(card));
    }

    /**
     * 카드 삭제
     *
     * @param authUser : 사용자 ID, email, 권한이 담긴 객체
     * @param cardId : card id
     * @param listId : list id
     */
    @Transactional
    public void deleteCard(Long listId, AuthUser authUser, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        if(card.isDeleted()){
            throw new InvalidRequestException("이미 삭제된 카드입니다.");
        }

        List list= listRepository.findById(listId).orElseThrow(() -> new InvalidRequestException("리스트를 찾을 수 없습니다."));
        Member member = checkRole(list, authUser); // 권한 확인

        if(member.getMemberRole().equals(MemberRole.READ_ONLY)){
            throw new InvalidRequestException("권한이 없습니다.");
        }

        card.deleteCard();
        cardRepository.save(card);
    }

    // 권한 확인
    private Member checkRole(List list, AuthUser authUser){
        Long workSpaceId = list.getBoard().getWorkSpace().getId();
        Member member = memberRepository.findByWorkSpace_IdAndUser_Id(workSpaceId, authUser.getId())
                .orElseThrow(() -> new InvalidRequestException("해당 워크스페이스에 초대되지 않았습니다."));

        return member;
    }
}
