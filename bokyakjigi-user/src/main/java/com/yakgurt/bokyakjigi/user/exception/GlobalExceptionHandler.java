package com.yakgurt.bokyakjigi.user.exception;

import com.yakgurt.bokyakjigi.user.common.response.StatusCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * 전역적으로 예외를 처리해주는 클래스
 * TODO : 다국어 처리 고려(message에 메시지 코드 넣고 MessageSource로 메시지 resolve)
 */
@Slf4j
@RestControllerAdvice // 전역 예외 처리용 어노테이션(@ControllerAdvice + @ResponseBody 역할), 모든 컨트롤러에서 발생하는 예외를 잡아 JSON형태로 응답을 만들어 준다(그래야 프런트에서 파싱이 가능함)
public class GlobalExceptionHandler {
    
    //---> security/JwtProvider에서 던진 예외 처리
    /**
     * TokenExpiredException 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정함
     * 리턴 타입 ResponseEntity<T> : 스프링에서 HTTP 응답을 직접 제어할 수 있게 해주는 클래스
     * 상태코드(예: 401) + 헤더 + 바디 전체를 세세하게 통제하려고 사용함
     * @param ex 예외 객체 (TokenExpiredException)
     * @return 클라이언트에게 HTTP 상태코드 401과 예외 메시지를 함께 전달
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpired(TokenExpiredException ex){ //ResponseEntity<String> -> 에러 응답객체 DTO로 변경함
        log.warn("[JWT] 만료된 토큰으로 인한 예외 발생: {}", ex.getMessage(), ex);


        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), // 상태코드
                StatusCode.UNAUTHORIZED.name(), // 상태 타입
                ex.getMessage(), // 예외 메세지
                ZonedDateTime.now(ZoneOffset.UTC) // 예외 발생 시각
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
//        //클라이언트에게 HTTP 응답 보냄
//        return  ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED) // 상태코드: 401 UNAUTHORIZED (인증 실패)
//                .body(ex.getMessage());          // 응답 본문(body)에 메시지 담기
    }

    /**
     * InvalidTokenException 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * JWT 토큰 구조가 잘못됐거나 위조된 경우 등 토큰 관련 예외처리
     * @param ex 예외 객체(InvalidTokenException)
     * @return HTTP 응답 객체(상태 코드 401 + 예외메세지)
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex){
        log.warn("[JWT] 유효하지 않은 토큰 예외 발생 : {}", ex.getMessage(),ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), // 상태 코드
                StatusCode.UNAUTHORIZED.name(), // 상태 타입
                ex.getMessage(), // 예외 메세지
                ZonedDateTime.now(ZoneOffset.UTC) // 예외 발생 시각
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
       /* return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 상태 코드 : 401
                .body(ex.getMessage()); //응답 본문(body)에 메세지 담음  */
    }

    /**
     * MissingUserClaimException 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * JWT 토큰 내에 사용자 정보 클레임이 없을 때 발생하는 예외 처리
     * HTTP 상태코드 400 Bad Request반환(클라이언트 잘못된 요청)
     * @param ex 예외 객체(MissingUserClaimException)
     * @return HTTP 응답 객체(상태 코드 400 + 예외 메세지)
     */
    @ExceptionHandler(MissingUserClaimException.class)
    public ResponseEntity<ErrorResponse> handleMissingUserClaim(MissingUserClaimException ex){
        log.warn("[JWT] 사용자 정보 누락 예외 발생 : {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                StatusCode.BAD_REQUEST.name(),
                ex.getMessage(),
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

       /* return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 상태 코드 400
                .body(ex.getMessage()); */
    }
    //<---

    //------> signIn 메서드에서 발생하는 예외 처리

    /**
     * UsernameNotFoundException예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * 사용자가 시도한 로그인 정보(email)이 일치 하지 않을 때, 그리고 이미 탈퇴한 회원인 경우 발생하는 예외 처리
     * HTTP 상태코드 401 UNAUTHORIZED 반환(인증실패), 아이디/비밀번호 틀림
     * @param ex 예외 객체(UsernameNotFoundException)
     * @return HTTP 응답 객체(상태 코드 401 + 예외메세지) - 보안을 위해서 ex.getMessage() 보내지 않음
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex){
        log.warn("[Sign-in] 로그인 사용자 정보 없음(사용자 조회 실패) 예외 발생 : {}",ex.getMessage(),ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                StatusCode.UNAUTHORIZED.name(),
                "아이디 또는 비밀번호가 올바르지 않습니다.",
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * BadCredentialsException 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * 사용자가 시도한 로그인 정보(password)가 일치 하지 않을 때 발생하는 예외 처리
     * HTTP 상태코드 401 UNAUTHORIZED 반환(인증실패), 아이디/비밀번호 틀림
     * @param ex 예외 객체(BadCredentialsException)
     * @return HTTP 응답 객체(상태 코드 401 + 예외메세지) - 보안을 위해서 ex.getMessage() 보내지 않음
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex){
        log.warn("[Sign-in] 로그인 사용자의 비밀번호 불일치 예외 발생 : {}", ex.getMessage(),ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                StatusCode.UNAUTHORIZED.name(),
                "아이디 또는 비밀번호가 올바르지 않습니다.",
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * HttpMediaTypeNotSupportedException 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * 클라이언트가 지원하지 않는(JSON이 아닌) Content-Type으로 요청을 보냈을 경우 발생하는 예외 처리 (예: application/xml 등)
     * HTTP 상태코드 415 UNSUPPORTED_MEDIA_TYPE 반환
     * @param ex 예외 객체(HttpMediaTypeNotSupportedException)
     * @param request HTTP 요청 정보 (URI, 메서드 등)
     * @return HTTP 응답 객체(상태 코드 415 + 예외메세지) - 보안을 위해서 ex.getMessage() 보내지 않음
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                 HttpServletRequest request) {
        log.warn("[지원하지 않는 미디어 타입] 요청 URI: {}, Method: {}, 예외 메시지: {}",
                request.getRequestURI(), request.getMethod(), ex.getMessage());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                StatusCode.UNSUPPORTED_MEDIA_TYPE.name(),
                "지원하지 않는 미디어 타입입니다. JSON 형식으로 보내주세요.",
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    /**
     * HttpMessageNotReadableException 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * JSON 파싱 실패 시 발생하는 예외(잘못된 JSON 포맷, 타입 불일치, 누락된 필드 등)
     * HTTP 상태 코드 400 BAD_REQUEST 반환
     * @param ex 예외 객체(HttpMessageNotReadableException)
     * @param request HTTP 요청 정보 (URI, 메서드 등)
     * @return HTTP 응답 객체(상태 코드 400 + 예외메세지) - 보안을 위해서 ex.getMessage() 보내지 않음
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("[잘못된 JSON 형식] 요청 URI: {}, Method: {}, 클라이언트 IP: {}, 예외 메시지: {}",
                request.getRequestURI(), request.getMethod(), request.getRemoteAddr(), ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                StatusCode.BAD_REQUEST.name(),
                "잘못된 JSON 형식입니다.",
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    //<--------


    /**
     * Exception 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * 기타 모든 예외 처리 메서드
     * 예상하지 못한 예외를 잡아 HTTP 상태코드 500 Internal Server Error 반환
     * 에러 로그(error)로 남겨 문제 원인 추적 가능하게 함
     *
     * @param ex 예외 객체(Exception)
     * @return HTTP 응답 객체(상태 코드 500 + 예외 메세지) - 보안을 위해서 ex.getMessage() 보내지 않음
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
        log.error("예상치 못한 예외 발생: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                StatusCode.INTERNAL_SERVER_ERROR.name(),
                "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        /* return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 상태 코드 500
                .body("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."); // 보안 때문에 .body(ex.getMessage());를 사용하지 않음
        // 내부 시스템 정보 노출 방지
         */
    }


    //---> SignUp에서 발생하는 예외처리

    /**
     * DuplicateEmailException(커스텀) 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * DB에 중복된 이메일이 있으면 발생하는 예외 처리 메서드
     * @param ex 예외 객체(DuplicateEmailException)
     * @return HTTP 응답 객체(상태 코드 400 + 예외 메세지) - - 보안을 위해서 ex.getMessage() 보내지 않음
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public  ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        log.warn("이메일 중복 예외: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                StatusCode.BAD_REQUEST.name(),
                "이미 사용 중인 이메일입니다.", //-> 보안상 ex.getMessage() 보내지 않음
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * DuplicateNicknameException(커스텀) 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * DB에 중복된 이메일이 있으면 발생하는 예외 처리 메서드
     * @param ex 예외 객체(DuplicateNicknameException)
     * @return HTTP 응답 객체(상태 코드 400 + 예외 메세지) - - 보안을 위해서 ex.getMessage() 보내지 않음
     */
    @ExceptionHandler(DuplicateNicknameException.class)
    public  ResponseEntity<ErrorResponse> handleDuplicateNickname(DuplicateNicknameException ex) {
        log.warn("닉네임 중복 예외: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                StatusCode.BAD_REQUEST.name(),
                "이미 사용 중인 닉네임입니다.", //-> 보안상 ex.getMessage() 보내지 않음
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //<---
}
