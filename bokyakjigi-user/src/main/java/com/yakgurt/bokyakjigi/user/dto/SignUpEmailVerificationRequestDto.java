package com.yakgurt.bokyakjigi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 이메일 인증 검증용 요청 dto
 * - 회원 가입 완료 후 인증 처리하는 거라서 토큰 + memberId도 받아야함(PK)
 */
@Getter @NoArgsConstructor @AllArgsConstructor
@Schema(description = "회원가입 이메일 인증용 DTO")
public class SignUpEmailVerificationRequestDto {

    @Schema(description = "인증 요청한 회원의 PK", example = "1")
    @NotNull(message = "memberId는 필수입니다.") //-> NotBlank는 문자열 전용 검증
    // 공백이나 빈문자열 들어오면 타입 불일치로 스프링이 400에러 보냄(그렇지만 예외 응답 통일 위해 전역 예외처리에 따로 작성해서 처리할 수도 있다) -> 해둠
    @Positive(message = "memberId는 양수여야 합니다.")
    private Long memberId; // 인증 요청한 회원의 PK

    @Schema(description = "사용자가 이메일에서 받은 토큰을 입력한 값", example = "abc123token")
    @NotBlank(message = "사용자가 입력한 token은 필수 입니다") @NotNull
    private String token; // 사용자가 입력한 토큰 값 -> 서버에서 보낸 토큰과 일치하는지 Redis에 저장한 토큰과 비교해서 검증해야함
    // 사용자가 맞게 입력했는지
}
