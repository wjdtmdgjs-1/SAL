package com.sparta.sal.domain.board.entity;

import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.list.entity.List;
import com.sparta.sal.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

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

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<List> lists = new ArrayList<>();

    @NotNull
    private String boardTitle;
    private String background;

    public Board(WorkSpace workSpace, String boardTitle, String background) {
        this.workSpace = workSpace;
        this.boardTitle = boardTitle;
        this.background = background;
    }

    public void update(String boardTitle, String background) {
        this.boardTitle = boardTitle;
        this.background = background;
    }

    public void addList(List list) {
        lists.add(list);
        list.setBoard(this); // 리스트와 보드 간의 관계 설정
    }

    public void removeList(List list) {
        lists.remove(list);
        list.setBoard(null); // 리스트와 보드 간의 관계 해제
    }
}
