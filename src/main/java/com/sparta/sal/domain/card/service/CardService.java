package com.sparta.sal.domain.card.service;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.exception.InvalidRequestException;
import com.sparta.sal.domain.card.dto.request.ModifyCardRequest;
import com.sparta.sal.domain.card.dto.request.SaveCardRequest;
import com.sparta.sal.domain.card.dto.response.GetCardResponse;
import com.sparta.sal.domain.card.dto.response.ModifyCardResponse;
import com.sparta.sal.domain.card.dto.response.SaveCardResponse;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.card.repository.CardRepository;
import com.sparta.sal.domain.list.entity.List;
import com.sparta.sal.domain.list.repository.ListRepository;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CardService {
    private final CardRepository cardRepository;
    private final ListRepository listRepository;
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public SaveCardResponse saveCard(SaveCardRequest reqDto, Long listId, AuthUser authUser) {
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new InvalidRequestException("리스트를 찾을 수 없습니다."));
        Member member = checkRole(list, authUser);

        if (member.getMemberRole().equals(MemberRole.READ_ONLY)) {
            throw new InvalidRequestException("권한이 없습니다.");
        }

        Card card = new Card(reqDto);
        card.addList(list);
        Card saveCard = cardRepository.save(card);
        return new SaveCardResponse(saveCard);
    }

    public GetCardResponse getCard(AuthUser authUser, Long listId, Long cardId) {
        List list = listRepository.findById(listId)
                .orElseThrow(() -> new InvalidRequestException("리스트를 찾을 수 없습니다."));
        checkRole(list, authUser);
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        if (card.isDeleted()) {
            throw new InvalidRequestException("삭제된 카드입니다.");
        }

        viewCard(listId, cardId, authUser);
        Long views = getViewCard(listId, cardId);
        return new GetCardResponse(card, views);
    }

    @Transactional
    public ModifyCardResponse modifyCard(Long listId, AuthUser authUser, Long cardId, ModifyCardRequest reqDto) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        if (card.isDeleted()) {
            throw new InvalidRequestException("삭제된 카드입니다.");
        }

        List list = listRepository.findById(listId)
                .orElseThrow(() -> new InvalidRequestException("리스트를 찾을 수 없습니다."));
        Member member = checkRole(list, authUser);

        if (member.getMemberRole().equals(MemberRole.READ_ONLY)) {
            throw new InvalidRequestException("권한이 없습니다.");
        }

        card.modifyCard(reqDto);
        return new ModifyCardResponse(cardRepository.save(card));
    }

    @Transactional
    public void deleteCard(Long listId, AuthUser authUser, Long cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new InvalidRequestException("카드를 찾을 수 없습니다."));

        if (card.isDeleted()) {
            throw new InvalidRequestException("이미 삭제된 카드입니다.");
        }

        List list = listRepository.findById(listId)
                .orElseThrow(() -> new InvalidRequestException("리스트를 찾을 수 없습니다."));
        Member member = checkRole(list, authUser);

        if (member.getMemberRole().equals(MemberRole.READ_ONLY)) {
            throw new InvalidRequestException("권한이 없습니다.");
        }

        card.deleteCard();
        cardRepository.save(card);
    }

    public void viewCard(Long listId, Long cardId, AuthUser authUser) {
        Long userId = authUser.getId();

        String viewKey = generateViewKey(listId, cardId);
        String abuseKey = generateAbuseKey(userId, listId, cardId);
        String rankingKey = generateRankingKey(listId);

        // 어뷰징 방지
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(abuseKey, 1, 60, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(isNew)) {
            return;
        }

        // 조회수 증가
        redisTemplate.opsForValue().increment(viewKey);

        // ZSET에 랭킹 점수 증가
        redisTemplate.opsForZSet().incrementScore(rankingKey, cardId.toString(), 1);
    }


    public Long getViewCard(Long listId, Long cardId) {
        String viewKey = generateViewKey(listId, cardId);
        Object redisValue = redisTemplate.opsForValue().get(viewKey);
        return redisValue == null ? 0L : ((Number) redisValue).longValue();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void resetViewCount() {
        Set<String> viewKeys = redisTemplate.keys("list:*:card:*:view");
        Set<String> rankingKeys = redisTemplate.keys("list:*:ranking");

        if (viewKeys != null && !viewKeys.isEmpty()) {
            redisTemplate.delete(viewKeys);
            log.info("조회수와 랭킹이 자정에 초기화되었습니다.");
        }

        if (rankingKeys != null && !rankingKeys.isEmpty()) {
            redisTemplate.delete(rankingKeys);
        } else {
            log.info("초기화할 조회수 데이터가 없습니다.");
        }
    }

    public java.util.List<GetCardResponse> getTopRankedCards(Long listId, int top) {
        String rankingKey = generateRankingKey(listId);
        Set<Object> topCards = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, top - 1);

        java.util.List<Long> cardIds = new ArrayList<>();
        for (Object cardIdObj : topCards) {
            cardIds.add(Long.valueOf(cardIdObj.toString()));
        }

        java.util.List<Card> cards = cardRepository.findAllById(cardIds);
        java.util.List<GetCardResponse> result = new ArrayList<>();

        for (Card card : cards) {
            Double score = redisTemplate.opsForZSet().score(rankingKey, card.getId().toString());
            Long views = (score != null) ? score.longValue() : 0L;

            GetCardResponse cardResponse = new GetCardResponse(card, views);
            result.add(cardResponse);
        }
        return result;
    }

    private Member checkRole(List list, AuthUser authUser) {
        Long workSpaceId = list.getBoard().getWorkSpace().getId();
        return memberRepository.findByWorkSpace_IdAndUser_Id(workSpaceId, authUser.getId())
                .orElseThrow(() -> new InvalidRequestException("해당 워크스페이스에 초대되지 않았습니다."));
    }

    private String generateViewKey(Long listId, Long cardId) {
        return "list:" + listId + ":card:" + cardId + ":view";
    }

    private String generateAbuseKey(Long userId, Long listId, Long cardId) {
        return "user:" + userId + ":list:" + listId + ":card:" + cardId;
    }

    private String generateRankingKey(Long listId) {
        return "list:" + listId + ":ranking";
    }
}