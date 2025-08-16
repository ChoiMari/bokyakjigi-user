package com.yakgurt.bokyakjigi.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 로그아웃 요청 DTO
 * - 클라이언트는 별도의 memberId를 보내지 않고,
 *   Authorization 헤더에 담긴 JWT만 전달
 */
@Getter @NoArgsConstructor @AllArgsConstructor
public class SignOutRequestDto {
    @NotNull @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken; // 클라이언트가 가지고 있는 refresh token
}
