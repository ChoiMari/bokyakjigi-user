package com.yakgurt.bokyakjigi.user.web;

import com.yakgurt.bokyakjigi.user.common.response.ApiResponse;
import com.yakgurt.bokyakjigi.user.common.response.StatusCode;
import com.yakgurt.bokyakjigi.user.dto.SignUpRequestDto;
import com.yakgurt.bokyakjigi.user.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 회원가입 API
     * 클라이언트(리액트)에서 전달받은 회원가입 요청 데이터를 받아 회원 가입을 처리
     * @param dto 회원가입 요청 데이터
     * @return 가입된 회원의 고유 ID를 응답 data에 포함하여 json형태로 반환 / 회원가입 실패 시에는 예외 처리기에 의해 적절한 에러 응답 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Long>> signup(@Valid @RequestBody SignUpRequestDto dto) {
        log.debug("signup 요청 email(masked)={}, nickname={}", dto.getMaskedEmail(),dto.getNickname());

        Long memberId = signUpSvc.signUp(dto);

        ApiResponse<Long> response = new ApiResponse<>(
                HttpStatus.CREATED.value(), // 201 Created
                StatusCode.CREATED.name(),
                "회원가입 성공",
                memberId,  // 데이터: 가입한 회원 PK
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        return ResponseEntity.status(StatusCode.CREATED.getHttpStatus()).body(response);
    }

}
