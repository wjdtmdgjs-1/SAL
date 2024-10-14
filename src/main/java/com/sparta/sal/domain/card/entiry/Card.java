package com.sparta.sal.domain.card.entiry;

import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.assignee.entity.Assignee;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "card")
public class Card extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardTitle;
    private String cardExplain;
    private LocalDateTime deadline;
    private String attachment;

    @OneToMany(mappedBy = "card", cascade = CascadeType.REMOVE)
    private  List<Assignee> assignees = new ArrayList<>();



}
