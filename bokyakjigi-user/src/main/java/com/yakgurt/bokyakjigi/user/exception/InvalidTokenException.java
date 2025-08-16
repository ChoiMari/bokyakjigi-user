package com.yakgurt.bokyakjigi.user.exception;

/**
 * JWT 토큰이 위조되었거나 유효하지 않을 때 발생시키는 예외 클래스
 * 서명 불일치, 토큰 변조, 형식 오류 등을 포함
 */
public class InvalidTokenException extends JwtExceptionBase {
    /**
     * 메시지와 원인 예외를 함께 받아 예외 생성
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidTokenException(String message) {
        super(message);
    }
}
