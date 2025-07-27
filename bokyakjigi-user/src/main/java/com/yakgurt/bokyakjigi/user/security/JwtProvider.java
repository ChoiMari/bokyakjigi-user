package com.yakgurt.bokyakjigi.user.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yakgurt.bokyakjigi.user.config.jwt.JwtProperties;
import com.yakgurt.bokyakjigi.user.exception.InvalidTokenException;
import com.yakgurt.bokyakjigi.user.exception.MissingUserClaimException;
import com.yakgurt.bokyakjigi.user.exception.TokenExpiredException;
import com.yakgurt.bokyakjigi.user.vo.MemberVO;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
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
    private SecretKey secretKey; // JWT 서명(암호화)에 사용할 비밀 키 객체
    private final ObjectMapper objectMapper; // JSON 파싱용(json과 java객체 간 변환을 담당하는 클래스)

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
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // JWT 헤더에 타입 명시
                .setIssuer(jwtProperties.getIssuer()) // 토큰 발급자
                .setIssuedAt(now) // 토큰 발급 시간
                .setExpiration(expiryDate) // 토큰 만료시간
                .setSubject(memberVO.getEmail()) // 사용자 식별자(사용자 로그인 id)
                .setClaims(Map.of("user", memberVO))  // 페이로드(클레임즈)설정. user라는 키로 memberVO 전체를 담음
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명용 비밀키 및 알고리즘 설정,시그니처 설정(위변조 방지 위해  HMAC-SHA256으로 암호화)
                .compact(); // 최종적으로 JWT 문자열 생성해서 리턴
    }


    /**
     *JWT 토큰에서 페이로드 부분을 파싱, 사용자 정보(claims)를 가져오는 메서드
     * @param token HttpRequest Header로 전달된 파싱할 JWT 토큰 문자열
     * @return MemberVO 객체(로그인한 사용자 정보)
     * @throws JsonProcessingException JSON 변환 과정에서 예외 발생 가능
     * @throws IllegalArgumentException 토큰이 유효하지 않거나 만료 시 발생
     */
    public MemberVO getUserFromToken(String token) throws JsonProcessingException {
        try{
            // JWT 토큰에서 claims(페이로드) 추출
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)            // 서명 키 설정
                    .build()                          // JwtParser 생성
                    .parseSignedClaims(token)         // 토큰 파싱 + 서명 검증(검증 실패시 예외 발생)
                    .getPayload();                    // Claims(페이로드) 반환

            // 클레임즈에서 user 클래임 추출
            Object userObj = claims.get("user"); //JWT 페이로드에서 "user"키에 담긴 값을 꺼냄
            if (userObj == null) {
                // 사용자 정보가 누락된 경우 커스텀 예외로 던짐
                throw new MissingUserClaimException("토큰에 사용자 정보가 존재하지 않습니다.");
                // 변경 전 : throw new IllegalArgumentException("토큰에 사용자 정보가 없습니다.");
            }
            // Object -> JSON 문자열 변환
            String userJson = objectMapper.writeValueAsString(userObj);

            // JSON 문자열 -> MemberVO 변환
            MemberVO memberVO = objectMapper.readValue(userJson, MemberVO.class);

            return memberVO;

        } catch(ExpiredJwtException e) { //TODO : 던진 예외 처리하는 로직 추가
            throw new TokenExpiredException("만료된 토큰입니다.", e); // 토큰이 만료된 경우
            // 변경 전 : throw new IllegalArgumentException("만료된 토큰입니다.", e);
        } catch(JwtException | IllegalArgumentException e){
            // JWT 구조가 깨졌거나 시그니처가 위조되었거나 등
            throw new InvalidTokenException("유효하지 않은 토큰입니다.", e);
            // 변경 전 : throw new IllegalArgumentException("유효하지 않은 토큰입니다.", e);
        } catch (JsonProcessingException e) {
            // JSON 직렬화/역직렬화 중 에러
            throw new InvalidTokenException("사용자 정보를 파싱하는 도중 에러가 발생했습니다.", e);
        }

    }


}
