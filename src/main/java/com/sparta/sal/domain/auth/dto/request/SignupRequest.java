package com.sparta.sal.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {

    @NotBlank
    @Email
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[#?!@$%^&*-]).{8,15}$",
            message = "비밀번호는 8자 이상 15자 이하로, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개씩 포함해야 합니다"
    )
    @NotBlank
    private String password;

    @NotBlank
    private String userRole;

    @NotBlank
    private String name;
}
