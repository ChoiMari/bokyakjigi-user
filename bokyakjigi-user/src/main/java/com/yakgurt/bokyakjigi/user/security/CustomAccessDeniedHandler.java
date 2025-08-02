package com.yakgurt.bokyakjigi.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakgurt.bokyakjigi.user.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * 스프링 시큐리티 403 Forbidden 처리용 커스텀(AccessDeniedHandler를 implements)
 * 인증 되었지만(로그인 했음), 권한(role)이 없는 경우 실행됨(리소스 접근 불가)
 *
 * 굳이 커스텀으로 만든 이유 : 클라이언트(리액트)에 json형태로 에러메세지를 응답해 주어야 해서
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * 인증은 됐지만 권한 부족으로 접근 거부 시 호출되는 메서드
     * 서블릿 레벨에서 동작하므로 ResponseEntity를 직접 반환할 수 없고,
     * JSON 문자열을 직접 생성해 응답에 작성해야 함
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param accessDeniedException 권한 부족 예외 객체
     * @throws java.io.IOException IO 예외
     * @throws jakarta.servlet.ServletException 서블릿 예외
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 응답 콘텐츠 타입 JSON + UTF-8 인코딩 설정 (한글 깨짐 방지)
        response.setContentType("application/json;charset=UTF-8");
        // HTTP 상태코드 403 Forbidden 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        // ErrorResponse 객체 생성 (상태코드, 상태메시지, 사용자용 메시지, 예외 발생 시각)
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "FORBIDDEN",
                "접근 권한이 없습니다.",
                ZonedDateTime.now(ZoneOffset.UTC)
        );

        // Jackson ObjectMapper를 사용해 ErrorResponse를 JSON 문자열로 직렬화
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(errorResponse);

        // JSON 응답 본문에 작성하여 클라이언트로 전송
        response.getWriter().write(json);
    }
}
