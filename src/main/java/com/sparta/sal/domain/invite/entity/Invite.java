package com.sparta.sal.domain.invite.entity;

import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.member.entity.Member;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Entity
@Getter
@RequiredArgsConstructor
@Table(name = "invites")
public class Invite extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    private Long workSpaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Invite(User user, MemberRole memberRole, Long workSpaceId) {
        this.user=user;
        this.memberRole=memberRole;
        this.workSpaceId=workSpaceId;
    }

    public Invite(Member member, User user, MemberRole memberRole, Long workSpaceId) {
        this.member=member;
        this.user=user;
        this.memberRole=memberRole;
        this.workSpaceId=workSpaceId;
    }
}
