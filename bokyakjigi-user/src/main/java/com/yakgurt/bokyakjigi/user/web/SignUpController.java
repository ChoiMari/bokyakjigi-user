package com.yakgurt.bokyakjigi.user.web;

import com.yakgurt.bokyakjigi.user.common.response.ApiResponse;
import com.yakgurt.bokyakjigi.user.common.response.StatusCode;
import com.yakgurt.bokyakjigi.user.service.SignUpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class SignUpController {

    private final SignUpService signUpSvc;

    /**
     * 이메일 중복검사 API
     * @param email 중복 검사 대상 이메일(쿼리 파라미터로 전달)
     * @return 중복 없으면 true를 data에 포함하여 JSON 형태로 반환, 중복 시 서비스 계층에서 예외 발생하여 전역처리기에서 에러로 응답처리
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        log.info("checkEmail(email={})",email);
        signUpSvc.checkDuplicateEmail(email);
        return ResponseEntity.ok(new ApiResponse<>(StatusCode.OK.getHttpStatus().value(),
                StatusCode.OK.name(),
                "사용 가능한 이메일 입니다.",
                true,
                ZonedDateTime.now(ZoneOffset.UTC)));
    }

    /**
     * 닉네임 중복검사 API
     * @param nickname 중복 검사 대상 닉네임(쿼리 파라미터로 전달)
     * @return 중복 없으면 true를 data에 포함하여 JSON 형태로 반환, 중복 시 서비스 계층에서 예외 발생하여 전역처리기에서 에러로 응답처리
     */
    @GetMapping("/check-nickname")
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@RequestParam String nickname) {
        log.info("checkNickname(nickname={})",nickname);
        signUpSvc.checkDuplicateNickname(nickname);
        return ResponseEntity.ok(new ApiResponse<>(StatusCode.OK.getHttpStatus().value(),
                StatusCode.OK.name(),
                "사용 가능한 닉네임 입니다.",
                true,
                ZonedDateTime.now(ZoneOffset.UTC)));
    }

}
