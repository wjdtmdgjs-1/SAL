package com.sparta.sal.domain.workspace.entity;

import com.sparta.sal.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    public WorkSpace(Long makerId,String workSpaceTitle, String explain) {
        this.makerId=makerId;
        this.workSpaceTitle = workSpaceTitle;
        this.explains = explain;
    }
}
