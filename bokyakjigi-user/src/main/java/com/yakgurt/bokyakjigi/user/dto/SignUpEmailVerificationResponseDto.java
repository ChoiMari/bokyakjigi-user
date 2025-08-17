package com.yakgurt.bokyakjigi.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 이메일 인증 검증용 응답 dto
 */
@Getter @NoArgsConstructor @AllArgsConstructor
public class SignUpEmailVerificationResponseDto {
    /**
     * 인증 성공 여부
     */
    private boolean success;
    /**
     * 인증 결과 메시지
     */
    private  String message;
    /**
     * 로그인 가능 여부
     */
    private boolean loginAvailable;

}
