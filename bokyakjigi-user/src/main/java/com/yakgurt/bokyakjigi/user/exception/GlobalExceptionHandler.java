package com.yakgurt.bokyakjigi.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * 전역적으로 예외를 처리해주는 클래스
 * TODO : 추후 enum으로 상태 코드와 메세지 관리하는 로직으로 변경하기, 다국어 처리 고려
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
                "UNAUTHORIZED", // 상태 타입 // TODO : 추후 enum으로 뽑으면 깔끔함
                ex.getMessage(), // 예외 메세지
                LocalDateTime.now() // 예외 발생 시각
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
                "UNAUTHORIZED", // 상태 타입
                ex.getMessage(), // 예외 메세지
                LocalDateTime.now() // 예외 발생 시각
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
                "BAD_REQUEST",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

       /* return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // 상태 코드 400
                .body(ex.getMessage()); */
    }
    //<---

    /**
     * Exception 예외가 발생했을 때 이 메서드가 자동 호출되도록 지정
     * 기타 모든 예외 처리 메서드
     * 예상하지 못한 예외를 잡아 HTTP 상태코드 500 Internal Server Error 반환
     * 에러 로그(error)로 남겨 문제 원인 추적 가능하게 함
     *
     * @param ex 예외 객체(Exception)
     * @return HTTP 응답 객체(상태 코드 500 + 예외 메세지)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex) {
        log.error("예상치 못한 예외 발생: {}", ex.getMessage(), ex);
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.",
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        /* return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // 상태 코드 500
                .body("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요."); // 보안 때문에 .body(ex.getMessage());를 사용하지 않음
        // 내부 시스템 정보 노출 방지
         */
    }
}
