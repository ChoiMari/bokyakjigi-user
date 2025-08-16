package com.yakgurt.bokyakjigi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 새로운 Access Token으로 재발급 해주는 응답 DTO
 */
@Getter 
@NoArgsConstructor //-> 역직렬화에 필요함(객체 -> JSON)
@AllArgsConstructor
@Schema(description = "Access Token 재발급 응답 DTO")
public class AccessTokenRefreshResponseDto {

    @Schema(description = "새로 발급된 Access Token", example = "eyJ0eXAiOiJKV1Qi...")
    private String accessToken;

    @Schema(description = "기존의 Refresh Token", example = "eyJ0eXAiOiJKV1Q..")
    private String refreshToken; //-> 재발급하지 않음. → 만료되면 재로그인 처리.

    @Schema(description = "토큰 타입", example = "Bearer")
    private final String tokenType = "Bearer";

    // refresh token은 재발급 안함. -> 만료시 재로그인 처리
}
