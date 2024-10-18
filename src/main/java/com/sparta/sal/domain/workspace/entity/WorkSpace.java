package com.sparta.sal.domain.workspace.entity;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.board.entity.Board;
import com.sparta.sal.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "workspaces")
public class WorkSpace extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long makerId;

    private String workSpaceTitle;

    private String explains;

    private String slackChannel;

    @OneToMany(mappedBy = "workSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Member> memberList = new ArrayList<>();


    @OneToMany(mappedBy = "workSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boardList = new ArrayList<>();

    public WorkSpace(Long makerId, String workSpaceTitle, String explain) {
        this.makerId = makerId;
        this.workSpaceTitle = workSpaceTitle;
        this.explains = explain;
    }

    public void update(String workSpaceTitle, String explain) {
        this.workSpaceTitle = workSpaceTitle;
        this.explains = explain;
    }

    public void updateSlackChannel(String slackChannel) {
        this.slackChannel = slackChannel;
    }

    public boolean isMember(AuthUser authUser) {
        return memberList.stream()
                .anyMatch(member -> member.getUser().getId().equals(authUser.getId()));
    }
}
