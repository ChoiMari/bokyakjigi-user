package com.yakgurt.bokyakjigi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

/**
 * 이메일 인증 발송 응답 dto
 * - 회원가입/아이디찾기/비밀번호초기화/마이페이지 민감정보 접근에 사용
 */
@Getter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "이메일 인증 발송 응답 DTO")
public class SendEmailVerificationResponseDto {
    @Schema(description = "이메일 발송 성공 여부", example = "true")
    private boolean success;

    @Schema(description = "사용자에게 보여줄 메시지", example = "이메일이 성공적으로 발송되었습니다.")
    private String message;

    @Schema(description = "이메일 발송 시각", example = "2025-08-17T12:00:00Z")
    private ZonedDateTime sentAt;

    // 토큰은 보안상 프론트에 보내줄 필요없음

    /**
     * 토큰 유효 시간(초 단위)
     * 프론트에서 안내 메시지용으로 활용
     * 예시) 프론트에서 “인증 메일은 24시간 내에만 유효합니다” 라고 안내 가능 등 보여주기 위해서 응답으로 보냄
     */
    @Schema(description = "토큰 유효 시간(초 단위). 프론트 안내용", example = "86400")
    private Long remainingTTL;
}
