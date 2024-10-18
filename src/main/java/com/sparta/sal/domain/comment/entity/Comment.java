package com.sparta.sal.domain.comment.entity;

import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.card.entity.Card;
import com.sparta.sal.domain.comment.dto.request.ModifyCommentRequest;
import com.sparta.sal.domain.comment.dto.request.SaveCommentRequest;
import com.sparta.sal.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "comment")
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String commentContent;
    private String emoji;
    private boolean isDeleted=false;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment(SaveCommentRequest reqDto){
        this.commentContent=reqDto.getContents();
        this.emoji=reqDto.getEmoji();
    }

    public void ModifyComment(ModifyCommentRequest reqDto){
        this.commentContent=reqDto.getContents();
        this.emoji=reqDto.getEmoji();
    }

    public void deleteComment(){
        this.isDeleted=true;
    }

    public void addCard(Card card){
        this.card=card;
    }

    public void addUser(User user) {
        this.user = user;
    }
}
