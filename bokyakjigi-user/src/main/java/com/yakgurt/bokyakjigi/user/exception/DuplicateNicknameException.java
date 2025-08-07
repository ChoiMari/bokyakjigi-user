package com.yakgurt.bokyakjigi.user.exception;

/**
 * 닉네임 중복 예외 클래스
 */
public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException(String message) {
        super(message);
    }
}
