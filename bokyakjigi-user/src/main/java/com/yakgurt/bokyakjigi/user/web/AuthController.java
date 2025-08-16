package com.yakgurt.bokyakjigi.user.web;

import com.yakgurt.bokyakjigi.user.dto.SignInRequestDto;
import com.yakgurt.bokyakjigi.user.dto.SignInResponseDto;
import com.yakgurt.bokyakjigi.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 인증 관련 HTTP 요청을 처리하는 컨트롤러 클래스
 * 로그인 요청을 받아 JWT 토큰을 반환
 */
@RestController // REST API용 컨트롤러임을 나타냄. JSON으로 주고 받음
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    //private final AuthenticationManager authenticationManager;
    //private final JwtProvider jwtProvider; // JWT 토큰 생성, 검증 담당 클래스


    /**
     * 로그인 요청 처리 메서드
     * 프론트(리액트)에서 이메일, 비밀번호가 포함된 JSON 바디를 받음
     * @param dto 프론트(리액트)가 보내는 로그인 요청 dto
     * @param bindingResult 검증 오류 결과 객체, @Valid가 유효성 검사를 수행한 결과를 담는 객체(검증 오류가 있으면 그 내용이 여기에 들어감)
     * @return JWT 액세스 토큰 포함 JSON 응답(성공 시)
     *
     * @valid 스프링이 DTO에 붙은 검증 에너테이션을 실행하라고 지시하는 역할(요청 데이터가 올 때 유효성검사 자동으로 해줌)
     * @RequestBody HTTP 요청의 Body에 담긴 JSON 데이터를 자바 객체 DTO로 변환해달라는 의미(클라이언트가 보내는 JSON을 DTO객체로 매핑해줌)
     * 전역예외처리클래스 있어서 try-catch로 안 묶음
     */
    @Operation( summary = "로그인", description = "이메일과 비밀번호로 로그인하고 JWT 토큰을 발급받는다.", tags = {"Authentication"},
            responses = {
            @ApiResponse(responseCode = "200", description = "로그인 성공, JWT 토큰 반환",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = SignInResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청, 유효성 검사 실패",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "인증 실패, 잘못된 이메일 또는 비밀번호",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequestDto dto,
                                    BindingResult bindingResult){
        log.info("SignIn start (dto.email={},validationErrors={})",dto.getEmail(),bindingResult.hasErrors()); // 로그인 시도 비밀번호는 보안상 로그 안찍음
        // 요청 데이터 검증 에러 처리(유효성 검사 실패 시)
        if(bindingResult.hasErrors()){ // @Valid로 지시한 dto에 유효성 검사 시 에러가 발생하면 실행됨
            log.warn("유효성 검사 실패: {}", bindingResult.getAllErrors());
            // 검증 실패한 각 필드별 오류 정보를 스트림으로 처리함
            List<Map<String, String>> errorList = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> Map.of(
                            "field", fieldError.getField(), // 오류가 난 필드 이름을 field 키에 저장
                            "message", fieldError.getDefaultMessage() // 해당 필드에 대한 기본 오류 메시지를 message키에 저장
                    ))
                    .collect(Collectors.toList()); // Map객체들을 리스트로 수집하여 errorList에 저장

            return ResponseEntity.badRequest().body(errorList);
            // HTTP 400 상태 코드와 errorList를 JSON형태로 클라이언트(프론트서버)에 응답
        }

            // 유효성 검사에 문제 없으면 서비스에 로그인 요청 전달 -> JWT 토큰 발급
            SignInResponseDto response  = authService.signIn(dto.getEmail(),dto.getPassword());

            log.info("signIn success - email={}", dto.getEmail());

            // 200 OK 상태 코드와 함께 응답 반환
            return ResponseEntity.ok(response); // 스프링 부트는 내부적으로 Jackson(ObjectMapper) 를 사용해서 DTO → JSON 으로 자동 직렬화
            // @RestController + ResponseEntity.ok(dto) 조합 → JSON 자동 직렬화
    }

}
