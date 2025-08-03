package com.yakgurt.bokyakjigi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Sign Up 회원가입 요청 DTO
 * 프론트가 application/json으로 POST 요청 보낼 때 스프링에서 자동으로 이 DTO객체로 변환됨(잭슨라이브러리사용 +  Controller에서 @RequestBody 사용해야 함)
 */
@Getter @Setter @NoArgsConstructor
public class SignUpRequestDto {

    @Schema(description = "사용자 이메일", example = "user@example.com")
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 200, message = "이메일은 200자 이내여야 합니다.")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "유효한 이메일 주소를 입력해주세요."
    )
    private String email;

    @Schema(description = "비밀번호 (8~16자, 영문+숫자+특수문자 포함)", example = "password123@")
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8자 이상 16자 이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$",
            message = "비밀번호는 8~16자, 영문+숫자+특수문자를 포함해야 합니다.")
    private String password;

    @Schema(description = "닉네임 (50자 이내)", example = "후후후님")
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9_]+$", message = "닉네임은 한글, 영문, 숫자, _ 만 사용할 수 있습니다.")
    private String nickname;
}
