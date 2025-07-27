package com.yakgurt.bokyakjigi.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakgurt.bokyakjigi.user.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * AuthenticationEntryPoint 인터페이스를 구현.
 *  - 스프링 시큐리티에서 필터 체인 중 인증이 필요한 리소스에 인증 정보가 없거나 유효하지 않을 때 호출 되는 인터페이스
 *  보통 인증 실패시 401 상태 코드나 로그인 페이지로 리다이렉트 시키는 역할을 하는데
 *  프런트로 리액트를 사용하기 때문에(MSA) json 형태로 응답해야 해서 커스텀 클래스를 만듬(리다이렉트는 필요없음, 프런트로 상태코드 + 예외 메세지만 json형태로 잘 보내면 된다)
 *
 *  스프링 시큐리티에서 401 Unauthorized — AuthenticationEntryPoint 실행
 *  이때 스프링 시큐리티가 자동으로 등록된 AuthenticationEntryPoint를 호출하는데,
 *  SecurityFilterChain 설정에 등록해놓으면 스프링 시큐리티가 401 Unauthorized 상황에서 기본 구현체 대신 이 커스텀클래스를 실행해준다
 *
 *  이 클래스의 주요 역할 : 클라이언트(리액트)에 401 상태코드 + 에러메시지 응답(json 형태로)
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 로그인 하지 않은 사용자가 인증이 필요한 API 요청(JWT 인증 필요한 보호된 URL 접근)했을 때, 어떻게 응답할지 정의하는 메서드
     * 동작 흐름
     * [인증 안 된 사용자] → [보호된 URL 접근 시도] → [Spring Security 필터가 감지] → [commence() 호출됨] → [401 Unauthorized 응답 내려줌]
     *
     * 여기서는 ResponseEntity<ErrorResponse>를 직접 리턴 불가.
     * 왜냐면 이 메서드는 서블릿 레벨에서 동작하기 때문에,
     * Spring MVC의 컨트롤러 레이어로 진입조차 하지 못한 상태여서 @ExceptionHandler 이런거 쓸 수 없다
     * 
     * 그럼 어떻게 json으로 보내지?
     * 직접 json문자열을 만들거나 잭슨 오브젝트매퍼를 써서 수동 직렬화 해야함
     *
     * @param request
     * @param response
     * @param authException
     * @throws java.io.IOException
     * @throws ServletException
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 응답의 콘텐츠 타입을 JSON으로 설정 + UTF-8 인코딩 설정 (한글 깨짐 방지)
        response.setContentType("application/json;charset=UTF-8");
        // HTTP 상태코드 401 Unauthorized 설정
        // -> 인증 실패 상황(예: 토큰 없음, 만료됨, 잘못된 토큰 등)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(), // 상태코드
                "UNAUTHORIZED", // 상태 타입
                "인증되지 않은 요청입니다.",
                LocalDateTime.now() // 예외 발생 시각
        );

        // ObjectMapper 객체 생성
        // - Jackson 라이브러리에서 제공하는 직렬화 도구
        // - 자바 객체 → JSON 문자열로 변환할 때 사용
                ObjectMapper objectMapper = new ObjectMapper();

        // ErrorResponse 객체를 JSON 문자열로 변환 (직렬화)
        // - 내부적으로 필드명을 키로, 값을 밸류로 해서 JSON 포맷 문자열을 만든다
        //   예: { "status": 401, "error": "UNAUTHORIZED", "message": "...", "timestamp": "..." }
                String json = objectMapper.writeValueAsString(errorResponse);

        // 직렬화된 JSON 문자열을 응답 본문(response body)에 직접 작성해서 클라이언트에게 보냄
        // - ResponseEntity 사용 불가능한 위치이므로 이렇게 직접 출력 스트림에 작성해야 한다
                response.getWriter().write(json);

    }

}
