package com.yakgurt.bokyakjigi.user.exception;

/**
 * 이메일 형식이 올바르지 않을 때 던지는 예외
 */
public class EmailValidationException extends RuntimeException {
    public EmailValidationException(String message) {
        super(message);
    }
}
