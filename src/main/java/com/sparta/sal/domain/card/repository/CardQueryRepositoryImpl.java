package com.sparta.sal.domain.card.repository;

import com.sparta.sal.domain.assignee.entity.QAssignee;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.card.entity.QCard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class CardQueryRepositoryImpl implements CardQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Card> searchCards(Long boardId, String cardTitle, String cardExplain,
                                  LocalDate duedate, String assigneeName, Pageable pageable) {
        QCard card = QCard.card;
        QAssignee assignee = QAssignee.assignee;

        // 동적 조건 설정
        List<Card> results = queryFactory
                .selectFrom(card)
                .leftJoin(card.assignees, assignee)
                .where(
                        boardIdEq(boardId),
                        cardTitleContains(cardTitle),
                        cardExplainContains(cardExplain),
                        deadlineEq(duedate),
                        assigneeNameEq(assigneeName)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .selectFrom(card)
                .leftJoin(card.assignees, assignee)
                .where(
                        boardIdEq(boardId),
                        cardTitleContains(cardTitle),
                        cardExplainContains(cardExplain),
                        deadlineEq(duedate),
                        assigneeNameEq(assigneeName)
                )
                .fetchCount();

        return new PageImpl<>(results, pageable, total);
    }

    // 동적 조건 메서드들
    private BooleanExpression boardIdEq(Long boardId) {
        return boardId != null ? QCard.card.list.board.id.eq(boardId) : null;
    }

    private BooleanExpression cardTitleContains(String title) {
        return title != null ? QCard.card.cardTitle.containsIgnoreCase(title) : null;
    }

    private BooleanExpression cardExplainContains(String explain) {
        return explain != null ? QCard.card.cardExplain.containsIgnoreCase(explain) : null;
    }

    private BooleanExpression deadlineEq(LocalDate duedate) {
        return duedate != null ? QCard.card.deadline.eq(duedate.atStartOfDay()) : null;
    }

    private BooleanExpression assigneeNameEq(String assigneeName) {
        return assigneeName != null ? QAssignee.assignee.member.user.name.eq(assigneeName): null;
    }

}
