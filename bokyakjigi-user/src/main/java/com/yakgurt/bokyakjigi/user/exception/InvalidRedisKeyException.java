package com.yakgurt.bokyakjigi.user.exception;

/**
 * Redis에 저장된 key값이 유효하지 않을 때 발생하는 예외
 */
public class InvalidRedisKeyException extends RuntimeException{
    public InvalidRedisKeyException(String message) {
        super(message); // 부모(RuntimeException)에 message 전달해야 로그/응답에 표시됨
        // 예외객체.getMessage();하면 이 커스텀 예외 객체 생성하며 아규먼트로 넣은 메세지가 반환.
    }
}
