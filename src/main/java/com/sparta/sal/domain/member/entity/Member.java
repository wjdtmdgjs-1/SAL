package com.sparta.sal.domain.member.entity;

import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.member.enums.MemberRole;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "members")
public class Member extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MemberRole memberRole;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "workSpace")
    private WorkSpace workSpace;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user")
    private User user;

    public Member(User user, WorkSpace workSpace, MemberRole memberRole) {
        this.user=user;
        this.workSpace=workSpace;
        this.memberRole=memberRole;
    }

    public void update(MemberRole memberRole) {
        this.memberRole=memberRole;
    }
}
