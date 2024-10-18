package com.sparta.sal.domain.assignee.entity;

import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "assignee")
public class Assignee extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id")
    private Card card;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Assignee(Card card, Member member) {
        this.card = card;
        this.member = member;
    }

}
