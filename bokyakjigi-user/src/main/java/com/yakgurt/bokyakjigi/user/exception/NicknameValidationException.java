package com.yakgurt.bokyakjigi.user.exception;

/**
 * 닉네임 형식이 올바르지 않을 때 발생하는 커스텀 예외클래스
 */
public class NicknameValidationException extends RuntimeException {
    public NicknameValidationException(String message) {
        super(message);
    }
}
