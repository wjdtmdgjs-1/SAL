package com.sparta.sal.domain.workspace.entity;

import com.sparta.sal.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "workSpaces")
public class WorkSpace extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long createrId;

    private String workSpaceTitle;

    private String explain;

    public WorkSpace(Long createrId,String workSpaceTitle, String explain) {
        this.createrId=createrId;
        this.workSpaceTitle = workSpaceTitle;
        this.explain = explain;
    }
}
