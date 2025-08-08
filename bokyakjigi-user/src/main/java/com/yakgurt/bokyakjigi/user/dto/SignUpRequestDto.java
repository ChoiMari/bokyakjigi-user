package com.yakgurt.bokyakjigi.user.dto;

import com.yakgurt.bokyakjigi.user.domain.IsDeleted;
import com.yakgurt.bokyakjigi.user.domain.LoginType;
import com.yakgurt.bokyakjigi.user.domain.Member;
import com.yakgurt.bokyakjigi.user.domain.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Sign Up 회원가입 요청 DTO
 * 프론트가 application/json으로 POST 요청 보낼 때 스프링에서 자동으로 이 DTO객체로 변환됨(잭슨라이브러리사용 +  Controller에서 @RequestBody 사용해야 함)
 */
@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Schema(description = "닉네임 (50자 이내)", example = "후후후")
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9_]+$", message = "닉네임은 한글, 영문, 숫자, _ 만 사용할 수 있습니다.")
    private String nickname;

    /**
     * DTO → Entity 변환용 편의 메서드(일반 회원가입용)
      * @param
     * @return
     */
    public Member toEntity(String encodedPassword, Role role) {
        return Member.builder()
                .email(this.email)
                .password(encodedPassword)
                .nickname(this.nickname)
               // .createdAt(LocalDateTime.now())
               // .updatedAt(LocalDateTime.now())
              //  .isDeleted(IsDeleted.N) // -> member 도메인 클래스에 @PrePersist 메서드가 있어서 JPA가 엔티티를 DB에 저장하기 전에 자동으로 처리해줘서 명시적으로 세팅할 필요 없음
                .loginType(LoginType.LOCAL)
                .snsId(null) // 일반 회원가입은 SNS ID 없음
                .role(role)
                .build();
    }

    /**
     * 전송된 email 보안을 위해서 마스킹처리해서 로그찍음
     * @return 마스킹 처리된 이메일
     */
    public String getMaskedEmail() {
        if (email == null || !email.contains("@")) return "*****";
        String[] parts = email.split("@");
        String local = parts[0];
        String domain = parts[1];
        String maskedLocal = local.length() <= 2
                ? local.charAt(0) + "*"
                : local.substring(0, 2) + "*".repeat(local.length() - 2);
        return maskedLocal + "@" + domain;
    }

}
