package com.yakgurt.bokyakjigi.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Refresh Token으로 Access Token 재발급 요청 DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor //-> JSON 직렬화에 필요
@Schema(description = "클라이언트가 가진 Refresh Token으로 새로운 Access Token을 발급받기 위한 요청 DTO")
public class AccessTokenRefreshRequestDto {

    @NotBlank(message = "Refresh Token은 필수입니다.") // null , 빈문자열 , 공백문자열 안됨 //-> 컨트롤러에서 @Valid 사용 시 자동으로 검증, 실패시 BindingResult에 담김
    //-> bindingResult.getFieldError().getDefaultMessage()를 호출하면 @NotBlank에 적어둔 메시지가 반환. 이 메세지를 고대로 프론트에 예외 응답으로 보낼 수 있다!
    @Schema(description = "클라이언트가 저장한 Refresh Token", example = "eyJ0eXAiOi...")
    private String refreshToken;
}
