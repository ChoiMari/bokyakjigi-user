package com.yakgurt.bokyakjigi.user.security;
import java.io.IOException;
import java.util.List;


import com.yakgurt.bokyakjigi.user.exception.InvalidTokenException;
import com.yakgurt.bokyakjigi.user.vo.MemberVO;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT 토큰을 HTTP 요청마다 검사(검증)하는 필터 클래스
 * OncePerRequestFilter를 상속. 이유 : 요청 당 한 번만 필터가 실행 되도록 해주기 때문에
 *
 * JWT 토큰이 유효하면 인증 객체를 스프링 시큐리티 컨텍스트에 저장
 * 토큰 검증 실패 시 예외를 던지지 않고 다음 필터로 넘겨서 
 * 전역 예외 처리 클래스(또는 스프링 시큐리티)에 위임
 *
 * (처음 로그인 시)토큰 없으면 인증처리 무시하고 다음 필터 진행
 * → 인증 필요한 요청에서 JWT 없으면 Spring Security가 401 처리(/api/auth/login 같은 URL은 인증 없이 접근 허용 상태해놔서 401에러 없음, 걱정 안해도 된다)
 * → 로그인 시에는 JWT 없어도 인증 없이 요청이 허용
 */
@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    /**
     * JWT 토큰 추출 메서드
     * 요청 헤더의 Authorizaion에서 "Bearer " 접두어로 시작하는 토큰을 추출
     * [쉬운 설명] 클라이언트가 보낸 HTTP 요청 헤더 중, Authorizaion 헤더에서 "Bearer {토큰}" 형태로 된 토큰을 꺼낸다
     * 보통 JWT 인증은 "Bearer "라는 접두어를 붙여서 보내기 때문에 이 형식을 먼저 체크하고, 앞에 붙은  "Bearer "를 잘라내는 작업을 함
     * @param request HTTP 요청 객체
     * @return 토큰 문자열("Bearer "접두어 제외) 또는 null(헤더가 없거나 잘못된 형식일 경우)
     */
    private String resolveToken(HttpServletRequest request){
       String bearerToken = request.getHeader("Authorization");  // Authorization 헤더에서 값 꺼냄

        // "Bearer "로 시작하는 토큰인지 확인(JWT인증 방식인지)
        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            // 앞의 "Bearer " 7글자를 제외한 나머지 토큰만 잘라서 리턴
            return bearerToken.substring(7);
        }
        // 토큰이 없거나 형식이 잘못되었으면 null 반환
        return null;

    }

    /**
     * 모든 HTTP 요청에 대해 실행될 실제 필터 로직 메서드(요청 url이 /api/로 시작하는 조건문 추가함)
     * 토큰이 없을 때 처리는? 현재 구조에서는 /api/** 요청에 토큰이 없으면 아무 인증 없이 넘어감(-> 시큐리티 컨피그에서 제어/처리)
     * 토큰 해석해서 SecurityContextHolder에 사용자 인증 정보 저장하는 역할 수행
     * @param request HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param filterChain 다음 필터 또는 컨트롤러로 이어지는 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException IO(입출력) 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException { //throws ServletException, IOException는 스프링 시큐리티 필터 체인 내부에서 자동으로 처리해줌(전역 예외 처리기에서 잡을일이 거의없다)
        try{
            // 클라이언트가 요청한 서블릿 경로를 반환(컨텍스트 경로 제외한 부분만)
            String servletPath = request.getServletPath();

            // 요청 url이 /api/로 시작하는 경우만 처리하겠다.
            if(servletPath.startsWith("/api/")) {
                // Authorization 헤더에서 "Bearer "을 제거한 토큰 추출(형식이 잘못 되었음 null 리턴 받음)
                String token = resolveToken(request);
                // 토큰 없으면(처음로그인 시) 실행되지 않고 여기서 그냥 넘어가고 다음 필터로 전달
                if (token != null && jwtProvider.validateToken(token)) { // 토큰이 존재하고 유효한 경우-> 사용자 인증 처리
                    // 토큰에서 사용자 정보(역직렬화한 MemberVO 객체)를 추출(JWT 페이로드의 "user"클레임즈)
                    MemberVO memberVO = jwtProvider.getUserFromToken(token);
                    SecurityUser member = new SecurityUser(memberVO);

                    // 인증 객체 생성
                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(member, // 로그인한 사용자 객체(MemberVO)
                                    null, //JWT 인증할 땐 비밀번호 필요없음
                                    member.getAuthorities()); // 사용자 권한 리스트
                    // 시큐리티 컨텍스트에 인증 객체 저장 -> 이후 컨트롤러에서 @AuthenticationPrincipal 등 사용 가능
                    SecurityContextHolder.getContext().setAuthentication(auth);
                    log.debug("[JWT 필터 인증 성공]  사용자: {}, URI: {}", memberVO.getEmail(), request.getRequestURI());

                }

            }
        } catch (JwtException | IllegalArgumentException ex) {
            // 토큰 파싱 실패, 만료, 서명 불일치 등 → 예외 발생
            // 여기선 직접 응답을 반환하지 않고 로그만 남김 (실패 처리는 전역 예외 처리기에 위임)
            log.warn("[JWT 필터] 인증 실패 - 이유: {}", ex.getMessage());
            // 커스텀 예외로 감싸서 던짐
            throw new InvalidTokenException("유효하지 않은 JWT 토큰입니다.", ex);
        }
        //try-catch 바깥(인증 실패든 성공이든 다음 필터나 컨트롤러는 호출되어야 하기 때문)
        filterChain.doFilter(request, response); // 다음 필터로 계속 전달(인증 성공 여부와 관계없이 요청 흐름은 계속 진행됨)
    }

}
