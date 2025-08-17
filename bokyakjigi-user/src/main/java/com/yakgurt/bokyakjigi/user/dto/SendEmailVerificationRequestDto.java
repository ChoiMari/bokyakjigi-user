package com.yakgurt.bokyakjigi.user.dto;

import com.yakgurt.bokyakjigi.user.common.response.EmailVerificationPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 이메일 인증 발송 요청 dto
 * - 회원가입/아이디찾기/비밀번호초기화/마이페이지 민감정보 접근에 사용
 */
@Getter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "가입한 회원의 이메일 인증을 위한 이메일 발송용 DTO")
public class SendEmailVerificationRequestDto {

    @Schema(description = "인증 요청한 회원의 PK", example = "1")
    @NotNull(message = "memberId는 필수입니다.")
    @Positive(message = "memberId는 양수여야 합니다.")
    private Long memberId;  // 인증 요청한 회원의 PK

}
