package com.yakgurt.bokyakjigi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * 로그아웃 응답 DTO
 * 성공 여부와 메시지를 전달
 */
@Getter @NoArgsConstructor @AllArgsConstructor
public class SignOutResponseDto {
    private boolean success; // 로그아웃 처리 성공 여부
    private String message; // 로그아웃 결과 메시지
}
