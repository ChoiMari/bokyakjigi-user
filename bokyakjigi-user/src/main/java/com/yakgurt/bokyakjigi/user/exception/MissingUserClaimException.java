package com.yakgurt.bokyakjigi.user.exception;

/**
 * 커스텀 예외 클래스 : JWT 토큰에 필수 사용자 정보 클레임이 없을 때 발생시키는 예외 클래스
 */
public class MissingUserClaimException extends JwtExceptionBase  {

    /**
     * 메시지와 원인 예외를 함께 받아 예외 생성
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public MissingUserClaimException(String message, Throwable cause) {
        super(message, cause);
    }
}
