package com.sparta.sal.domain.board.entity;

import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.user.entity.User;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "boards")
public class Board extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @NotNull
    private String boardTitle;
    private String background;

    public Board(WorkSpace workSpace, String boardTitle, String background) {
        this.workSpace=workSpace;
        this.boardTitle=boardTitle;
        this.background =background;
    }

    public void update(String boardTitle, String background) {
        this.boardTitle=boardTitle;
        this.background=background;
    }
}
