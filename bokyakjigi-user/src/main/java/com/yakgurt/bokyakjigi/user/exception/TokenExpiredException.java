package com.yakgurt.bokyakjigi.user.exception;

/**
 * JWT 토큰이 만료되었을 때 발생시키는 예외 클래스
 * JwtExceptionBase를 상속하여 JWT 관련 예외임을 명시
 */
public class TokenExpiredException extends JwtExceptionBase {
    /**
     * 메시지와 원인 예외를 함께 받아 예외 생성
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
