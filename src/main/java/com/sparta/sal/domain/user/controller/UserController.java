package com.sparta.sal.domain.user.controller;

import com.sparta.sal.common.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/test")
    public AuthUser test(@AuthenticationPrincipal AuthUser authUser) {
        return authUser;
    }
}
