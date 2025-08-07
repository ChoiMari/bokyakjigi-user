package com.yakgurt.bokyakjigi.user.exception;

/**
 * DB에서 USER 권한을 찾을 수 없을 때 발생하는 예외 커스텀 클래스
 */
public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String message) {
        super(message);
    }
}
