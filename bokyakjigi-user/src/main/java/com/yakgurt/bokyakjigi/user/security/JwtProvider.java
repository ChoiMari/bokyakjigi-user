package com.yakgurt.bokyakjigi.user.security;

import com.yakgurt.bokyakjigi.user.config.jwt.JwtProperties;
import com.yakgurt.bokyakjigi.user.vo.MemberVO;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

/**
 * JWT 토큰 생성 및 검증, 파싱을 담당하는 컴포넌트 클래스
 * secretKey를 HMAC SHA 알고리즘용 Key 객체로 초기화
 *
 */
@Component //컴포넌트 등록 : 스프링이 이 클래스를 자동으로 빈으로 만들어서 관리해줌
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtProperties jwtProperties;
    private Key secretKey; // JWT 서명(암호화)에 사용할 비밀 키 객체

    /**
     * @PostConstruct 스프링 빈이 생성되고 의존성 주입이 완료된 직후에 자동으로 실행되는 메서드에 붙이는 어노테이션(초기화 작업 수행)
     * 스프링 빈 초기화 후 실행되며
     * 시크릿키를 base64 인코딩된 문자열에서 HMAC SHA 키 객체로 변환시킴
     */
    @PostConstruct
    public void init() {
        // 문자열로 된 비밀키를 바이트 배열로 변환 후 HMAC SHA 알고리즘용 키 객체로 생성함.
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 엑세스 토큰을 생성하는 메서드
     * @param expiredAt 토큰의 유효 기간을 나타내는 Duration객체
     * @param memberVO JWT토큰 페이로드에 담을 사용자 정보 객체
     * @return 생성된 JWT 액세스 토큰 문자열
     */
    public String generateAccessToken(Duration expiredAt, MemberVO memberVO) {
        Date now = new Date(); // 토큰 발급 시간
        // 토큰 만료 시간 계산(현재시간 + expiredAt 밀리초)
        Date expiryDate = new Date(now.getTime() + expiredAt.toMillis());

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer()) // 토큰 발급자
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(expiryDate) // 토큰 만료시간
                .setSubject(memberVO.getEmail()) // 사용자 식별자(사용자 로그인 id)
                .setClaims(Map.of("user", memberVO))  // 페이로드(클레임즈)설정. user라는 키로 memberVO 전체를 담음
                .signWith(secretKey, SignatureAlgorithm.HS256) // 시그니처 설정(위변조 방지 위해  HMAC-SHA256으로 암호화)
                .compact(); // 최종적으로 JWT 문자열 생성해서 리턴
    }




}
