package com.yakgurt.bokyakjigi.user.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * 에러 응답 객체 DTO : 프런트에 통일된 형식의 json으로 보내기 위해 응답 DTO를 정의
 *
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int code;         // HTTP 상태 코드
    private String error;     // 오류 타입 (예: Unauthorized)
    private String message;   // 상세 오류 메시지

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "UTC")
    private ZonedDateTime timestamp;
    //private LocalDateTime timestamp; // 예외 발생 시각

}


