package com.yakgurt.bokyakjigi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * sign in 응답 DTO
 * 로그인 성공 시 JWT 토큰을 클라이언트에 전달하기 위한 응답 객체
 * 액세스 토큰과 리프레시 토큰, 토큰 타입(Bearer)을 포함
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignInResponseDto {
    private String accessToken;
    private String refreshToken;
    private final String tokenType = "Bearer";
}

/*
* public SignInResponseDto(String accessToken) {
    this.accessToken = accessToken;
    this.tokenType = "Bearer"; // final이니까 고정됨
}
* */