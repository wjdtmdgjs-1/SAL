package com.sparta.sal.common.dto;

import com.sparta.sal.domain.user.enums.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class AuthUser {

    private final Long id;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;

    public static AuthUser from(Long id, String email, UserRole userRole) {
        return new AuthUser(id, email, List.of(new SimpleGrantedAuthority(userRole.name())));
    }
}
