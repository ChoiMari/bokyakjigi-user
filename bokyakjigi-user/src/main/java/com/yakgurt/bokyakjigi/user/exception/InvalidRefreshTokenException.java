package com.yakgurt.bokyakjigi.user.exception;

/**
 * RefreshToken이 Redis에서 조회되지 않을 때 발생하는 예외(위조 또는 만료)
 */
public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
