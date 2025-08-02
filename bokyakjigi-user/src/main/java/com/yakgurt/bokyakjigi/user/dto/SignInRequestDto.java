package com.yakgurt.bokyakjigi.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 *로그인 요청 DTO
 * 로그인 요청 시 클라이언트에서 보내는 데이터 객체
 * 이메일과 비밀번호를 받음
 * 입력값 검증용 어노테이션 추가
 */
@Data
public class SignInRequestDto {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.") //관대하기 때문에 추가 유효성 체크함
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "유효한 이메일 주소를 입력해주세요."
    )
    @Size(max = 200, message = "이메일은 200자 이내여야 합니다.")
    private String email;

    // 비밀번호 유효성 검증 에너테이션
    @NotBlank(message = "비밀번호는 필수 입력값입니다.") // 비밀번호가 null이거나 빈 문자열("") 또는 공백만 있는 경우
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.") // 길이가 8자 이상이어야 함. (최대 길이는 Pattern에서 제한)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$",
            message = "비밀번호는 8~16자, 영문+숫자+특수문자를 포함해야 합니다.")
    private String password;
}
