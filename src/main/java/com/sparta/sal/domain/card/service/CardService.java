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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardService {
    private final CardRepository cardRepository;

    /**
     * 카드 생성
     *
     * @param reqDto : 제목, 설명, 마감일, 첨부파일
     * @return SaveCardResponse : id, 제목, 설명, 마감일, 첨부파일
     */
    @Transactional
    public SaveCardResponse saveCard(SaveCardRequest reqDto) {

        // 예외 처리 필요 : 읽기 전용 역할을 가진 멤버가 카드를 생성/수정하려는 경우

        // 리스트 값 넣어야 됨
        Card card = new Card(reqDto);
        Card saveCard = cardRepository.save(card);
        return new SaveCardResponse(saveCard);
    }

    /**
     * 카드 상세 조회
     *
     * @param cardId : card id
     * @return GetCardResponse : id, 제목, 설명, 마감일, 첨부파일
     */
    public GetCardResponse getCard(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        // 삭제된 카드를 조회한 경우
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
    public ModifyCardResponse modifyCard(AuthUser authUser, Long cardId, ModifyCardRequest reqDto) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        // 삭제된 카드를 조회한 경우
        if(card.isDeleted()){
            throw new InvalidRequestException("삭제된 카드입니다.");
        }

        // 예외 처리 필요 : 읽기 전용 역할을 가진 멤버가 카드를 생성/수정하려는 경우

        card.modifyCard(reqDto);
        return new ModifyCardResponse(cardRepository.save(card));
    }

    /**
     * 유저 단건 조회
     * @param authUser : 사용자 ID, email, 권한이 담긴 객체
     * @param cardId : card id
     */
    @Transactional
    public void deleteCard(AuthUser authUser, Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new InvalidRequestException("card not found"));

        // 예외 처리 필요 : 읽기 전용 역할을 가진 멤버가 카드를 삭제하려는 경우

        card.deleteCard();
        cardRepository.save(card);
    }
}
