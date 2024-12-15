package com.example.Biomatch.security.custom;

import com.example.Biomatch.user.domain.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomUserInfoDto {
    private Long userId;

    private String email;

    private String nickname;

    private String password;

    private RoleType role;
}