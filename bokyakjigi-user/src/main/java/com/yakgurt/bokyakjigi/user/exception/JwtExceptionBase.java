package com.yakgurt.bokyakjigi.user.exception;
/**
 * JWT 관련 커스텀 예외들의 최상위 부모 클래스
 * RuntimeException을 상속하여 언체크 예외로 동작(컴파일러가 예외처리를 강제하지 않는다)
 * 모든 JWT 관련 예외는 이 클래스를 상속하도록 설계
 *
 * Throwable (모든 예외와 에러의 최상위 클래스)
 * └─ Exception (일반 예외)
 * └─ RuntimeException (unchecked 예외)
 */
public class JwtExceptionBase extends RuntimeException {

    /**
     * 기본 메세지로 예외 생성
     * @param message 예외 메세지
     */
    public JwtExceptionBase(String message){
        super(message); // RuntimeException(부모클래스) 생성자 호출함, 이 메세지가 부모 객체에 저장됨(예외 메시지를 부모 클래스가 관리하도록 위임)
        // 예외 발생시 JwtExceptionBase가 던져지면, RuntimeException에 저장된 메시지가 getMessage()로 조회 가능
    }

    /**
     * 메세지와 원인 예외를 함께 전달 받아 예외 생성
     * @param message 예외 메세지
     * @param cause 발생 원인 예외 객체
     */
    public JwtExceptionBase(String message, Throwable cause) {
        super(message, cause);
    }
}
