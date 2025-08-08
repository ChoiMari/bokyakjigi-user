package com.yakgurt.bokyakjigi.user.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * HTTP 상태 코드를 enum 상수로 정의하여
 * 코드 가독성 향상 및 관리 통일
 * 각 상수는 spring의 HttpStatus 객체를 필드로 가지고 있어서
 * 상태 코드 값과 관련 메서드를 직접 사용할 수 있다.
 */
@Getter
@AllArgsConstructor
public enum StatusCode {
    // 2xx
    OK(HttpStatus.OK), // 200 요청 성공
    CREATED(HttpStatus.CREATED),

    // 4xx
    BAD_REQUEST(HttpStatus.BAD_REQUEST),// 400 잘못된 요청
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),// 401 인증 필요
    FORBIDDEN(HttpStatus.FORBIDDEN),// 403 권한 없음(접근 금지)
    NOT_FOUND(HttpStatus.NOT_FOUND),// 404 요청한 자원 없음
    CONFLICT(HttpStatus.CONFLICT), // 409 서버와 충돌 발생
    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE),// 415 미디어 타입 지원 안 함

    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR); // 500 서버 내부 오류

    private final HttpStatus httpStatus;
}
