package com.sparta.sal.domain.auth.controller;

import com.sparta.sal.domain.auth.dto.request.SigninRequest;
import com.sparta.sal.domain.auth.dto.request.SignupRequest;
import com.sparta.sal.domain.auth.dto.response.SigninResponse;
import com.sparta.sal.domain.auth.dto.response.SignupResponse;
import com.sparta.sal.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.signup(signupRequest));
    }

    @PostMapping("/auth/signin")
    public ResponseEntity<SigninResponse> signin(@Valid @RequestBody SigninRequest signinRequest) {
        return ResponseEntity.ok(authService.signin(signinRequest));
    }
}
