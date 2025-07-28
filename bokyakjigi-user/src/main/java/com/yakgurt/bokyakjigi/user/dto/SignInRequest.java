package com.yakgurt.bokyakjigi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *로그인 요청 DTO
 * 로그인 요청 시 클라이언트에서 보내는 데이터 객체
 * 이메일과 비밀번호를 받음
 * 입력값 검증용 어노테이션 추가
 */
@Data
public class SignInRequest {
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
}
