package com.yakgurt.bokyakjigi.user.web;

import com.yakgurt.bokyakjigi.user.dto.AccessTokenRefreshRequestDto;
import com.yakgurt.bokyakjigi.user.dto.AccessTokenRefreshResponseDto;
import com.yakgurt.bokyakjigi.user.exception.MissingRefreshTokenException;
import com.yakgurt.bokyakjigi.user.service.RefreshTokenRedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/auth")
@Slf4j @RequiredArgsConstructor
@Tag(name = "Token", description = "Access/Refresh Token 관리 API")
public class TokenController {
    private final RefreshTokenRedisService refreshSvc;

    /**
     * Refresh Token으로 새로운 Access Token(재발급)을 받기 위한 요청을 처리하는 메서드
     * @param dto
     * @param bindingResult
     * @return
     */
    @PostMapping("/token/refresh")
    @Operation(summary = "Access Token 재발급", description = "클라이언트가 보유한 Refresh Token으로 새로운 Access Token 발급")
    public ResponseEntity<?> refreshAccessToken(@Valid @RequestBody AccessTokenRefreshRequestDto dto,
                                                BindingResult bindingResult){
        log.debug("[Token] Refresh Token 재발급 요청 : {}", dto.getRefreshToken().substring(0,5) + "*****"); //-> 마스킹 처리해서 안전하게 로그 찍음
        if(bindingResult.hasErrors()) { // 요청dto 유효성 검사 실패 시 실행됨(null, 빈문자열, 공백)
            log.warn("[Token] Refresh Token 유효성 검사 실패 : {}", bindingResult.getAllErrors());
            String message = (bindingResult.getFieldError() != null) ? bindingResult.getFieldError().getDefaultMessage() : "[Refresh Token 예외]유효하지 않은 잘못된 요청입니다.";
            throw new MissingRefreshTokenException(message); //-> 예외 전역처리기에서 잡음
        }

        String refreshToken = dto.getRefreshToken();
        // 서비스 호출 -> access token 재발급
        String accessToken = refreshSvc.reissueAccessToken(refreshToken);
        log.info("[Token] Access Token 재발급 완료");
        //응답 DTO 생성 후 반환
        AccessTokenRefreshResponseDto response = new AccessTokenRefreshResponseDto(accessToken,dto.getRefreshToken());

        return ResponseEntity.ok(response);
    }
}
