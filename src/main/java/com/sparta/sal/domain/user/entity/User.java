package com.sparta.sal.domain.user.entity;

import com.sparta.sal.common.dto.AuthUser;
import com.sparta.sal.common.entity.Timestamped;
import com.sparta.sal.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "users")
public class User extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    private Boolean userStatus = true;

    private User(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public static User from(String email, String password, UserRole userRole) {
        return new User(email, password, userRole);
    }

    private User(Long id, String email, UserRole userRole) {
        this.id = id;
        this.email = email;
        this.userRole = userRole;
    }

    public static User fromAuthUser(AuthUser authUser) {
        return new User(authUser.getId(), authUser.getEmail(), UserRole.of(authUser.getAuthorities().stream().toList().get(0).getAuthority()));
    }

    public void withdrawUser() {
        this.userStatus = false;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}