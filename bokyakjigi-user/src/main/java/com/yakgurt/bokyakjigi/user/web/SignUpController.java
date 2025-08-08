package com.yakgurt.bokyakjigi.user.web;

import com.yakgurt.bokyakjigi.user.common.response.ApiResponse;
import com.yakgurt.bokyakjigi.user.common.response.StatusCode;
import com.yakgurt.bokyakjigi.user.dto.SignUpRequestDto;
import com.yakgurt.bokyakjigi.user.exception.EmailValidationException;
import com.yakgurt.bokyakjigi.user.exception.ErrorResponse;
import com.yakgurt.bokyakjigi.user.exception.NicknameValidationException;
import com.yakgurt.bokyakjigi.user.service.SignUpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "일반 회원가입 API")
public class SignUpController {

    private final SignUpService signUpSvc;
    public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String NICKNAME_REGEX = "^[가-힣a-zA-Z0-9_]+$";

    /**
     * 이메일 중복검사 API
     * @param email 중복 검사 대상 이메일(쿼리 파라미터로 전달)
     * @return 중복 없으면 true를 data에 포함하여 JSON 형태로 반환, 중복 시 서비스 계층에서 예외 발생하여 전역처리기에서 에러로 응답처리
     */
    @GetMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@Parameter(description = "중복 검사 대상 이메일", required = true, example = "test1234@test.com")
                                                                @RequestParam String email) {
        log.info("checkEmail(email={})",email);
        if (!email.matches(EMAIL_REGEX) || !EmailValidator.getInstance().isValid(email) || email.length() > 200 ||
                email == null || email.isBlank() ) {
            throw new EmailValidationException("이메일 형식이 올바르지 않습니다.");
        }

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
    public ResponseEntity<ApiResponse<Boolean>> checkNickname(@Parameter(description = "중복 검사 대상 닉네임", required = true, example = "후후후111")
                                                                  @RequestParam String nickname) {
        log.info("checkNickname(nickname={})",nickname);

        if (nickname == null || nickname.isBlank()
                || (nickname.length() < 2 || nickname.length() > 50) || !nickname.matches(NICKNAME_REGEX)) {
            throw new NicknameValidationException("닉네임은 2~50자의 한글, 영문, 숫자, _ 만 사용할 수 있습니다.");
        }

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
    @Operation(
            summary = "회원가입",
            description = "새로운 회원을 가입시킵니다. 성공 시 201 상태코드와 가입한 회원 ID를 반환합니다.",
            requestBody = @RequestBody(
                    required = true,
                    description = "회원가입 요청 데이터",
                    content = @Content(schema = @Schema(implementation = SignUpRequestDto.class))
            ),
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원가입 성공",
                            content = @Content(schema = @Schema(implementation = ApiResponse.class))
                    ),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                    )
            }
    )
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
