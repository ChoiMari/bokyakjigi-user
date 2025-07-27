package com.yakgurt.bokyakjigi.user.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역적으로 예외를 처리해주는 클래스
 */
@Slf4j
@RestControllerAdvice // 전역 예외 처리용 어노테이션(@ControllerAdvice + @ResponseBody 역할), 모든 컨트롤러에서 발생하는 예외를 잡아 JSON형태로 응답을 만들어 준다
public class GlobalExceptionHandler {
    
    //---> security/JwtProvider에서 던진 예외 처리
    @ExceptionHandler(IllegalArgumentException.class)

    //<---
}
