package com.yakgurt.bokyakjigi.user.exception;

/**
 * 이메일 중복 예외 클래스
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
