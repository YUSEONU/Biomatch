package com.example.Biomatch.user.responseDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TokenResponse {

    private String accessToken;           // Access Token

    // 성공적인 로그인 시 토큰만 반환하는 생성자
    public TokenResponse(String accessToken) {
        this.accessToken = accessToken;
    }

}
