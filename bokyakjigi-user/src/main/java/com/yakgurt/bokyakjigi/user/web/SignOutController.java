package com.yakgurt.bokyakjigi.user.web;

import com.yakgurt.bokyakjigi.user.dto.SignOutRequestDto;
import com.yakgurt.bokyakjigi.user.dto.SignOutResponseDto;
import com.yakgurt.bokyakjigi.user.service.SignOutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @Slf4j
@RequiredArgsConstructor @RequestMapping("/api/auth")
@Tag(name = "Auth", description = "인증 관련 API")
public class SignOutController {
    private final SignOutService signOutSvc;
    /**
     * 
     * @param dto 로그아웃 요청 DTO에 담긴 refreshToken 사용
     * @return 로그아웃 결과 (성공여부 & 메시지)
     */
    @PostMapping("/signout")
    @Operation(summary = "로그아웃", description = "Refresh Token을 이용해 Redis에서 로그아웃 처리")
    public ResponseEntity<SignOutResponseDto> signOut(@Valid @RequestBody SignOutRequestDto dto){
        Boolean result = signOutSvc.signOut(dto.getRefreshToken());
        if(result) {
            // 로그아웃 성공
            return ResponseEntity.ok(new SignOutResponseDto(true, "로그아웃 성공"));
        } else {
            // 이미 로그아웃했거나 token 없음
            return ResponseEntity.ok(new SignOutResponseDto(false, "이미 로그아웃했거나 token 없음"));
        }
    }
}
