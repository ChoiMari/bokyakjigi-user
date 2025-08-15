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
                .header() // ← Deprecated 아님!
                .add("typ", "JWT")
                .and()
                .claims() // ← 여기에 전체 클레임 입력
                .add("sub", String.valueOf(memberVO.getId())) // PK를 sub로, JWT 표준 (RFC 7519)에서는 sub를 문자열로 요구함
                .add("email", memberVO.getEmail())
                .add("iss", jwtProperties.getIssuer())
                .add("iat", now.getTime() / 1000)
                .add("exp", expiryDate.getTime() / 1000)
                .add("user", memberVO)
                .and()
                .signWith(secretKey)
                .compact();
//        return Jwts.builder()
//                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // JWT 헤더에 타입 명시
//                .setIssuer(jwtProperties.getIssuer()) // 토큰 발급자
//                .setIssuedAt(now) // 토큰 발급 시간
//                .setExpiration(expiryDate) // 토큰 만료시간
//                .setSubject(memberVO.getEmail()) // 사용자 식별자(사용자 로그인 id)
//                .setClaims(Map.of("user", memberVO))  // 페이로드(클레임즈)설정. user라는 키로 memberVO 전체를 담음
//                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명용 비밀키 및 알고리즘 설정,시그니처 설정(위변조 방지 위해  HMAC-SHA256으로 암호화)
//                .compact(); // 최종적으로 JWT 문자열 생성해서 리턴
    }


    /**
     * JWT 토큰에서 페이로드(claims)를 추출하고, 사용자 정보(claims)를 MemberVO 객체로 변환해서 반환하는 메서드
     * @param token HttpRequest Header로 전달된 파싱할 JWT 토큰 문자열 (token 클라이언트로부터 전달된 JWT 문자열 (Authorization 헤더 등에서 추출됨))
     * @return MemberVO (로그인한 사용자 정보가 담긴 객체)
     * @throws InvalidTokenException 토큰이 비정상적이거나 파싱 실패한 경우 발생
     * @throws TokenExpiredException 토큰이 만료된 경우 발생
     * @throws MissingUserClaimException 사용자 정보(user 클레임)이 존재하지 않을 경우 발생
     */
    public MemberVO getUserFromToken(String token) {
        try{
            // JWT 토큰에서 claims(페이로드) 추출
            Claims claims = Jwts.parser()             // 파서 생성
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

            // JSON 문자열 -> MemberVO 변환(역직렬화)
            MemberVO memberVO = objectMapper.readValue(userJson, MemberVO.class);

            return memberVO;

        } catch (JsonProcessingException e) {
            // JSON 직렬화/역직렬화 중 에러
            throw new InvalidTokenException("사용자 정보를 파싱하는 도중 에러가 발생했습니다.", e);
        } catch(ExpiredJwtException e) { // 던진 예외 처리하는 로직 : 커스텀 예외 클래스 -> 전역예외처리클래스(@RestControllerAdvice)
            throw new TokenExpiredException("만료된 토큰입니다.", e); // 토큰이 만료된 경우
            // 변경 전 : throw new IllegalArgumentException("만료된 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 형식
            throw new InvalidTokenException("지원되지 않는 JWT 형식입니다.", e);
        } catch (MalformedJwtException e) {
            // 잘못 구성된 JWT (예: 구문 오류, 구분자 부족 등)
            throw new InvalidTokenException("손상된 JWT 토큰입니다.", e);
        } catch (SecurityException e) {
            // 서명 검증 실패
            throw new InvalidTokenException("JWT 서명 검증에 실패했습니다.", e);
        } catch (ClassCastException e) {
            // 클레임 타입이 기대한 구조가 아님
            throw new InvalidTokenException("JWT 클레임 구조가 잘못되었습니다.", e);
        }  catch(JwtException | IllegalArgumentException e){
            // JWT 구조가 깨졌거나 시그니처가 위조되었거나 등
            throw new InvalidTokenException("유효하지 않은 토큰입니다.", e);
            // 변경 전 : throw new IllegalArgumentException("유효하지 않은 토큰입니다.", e);
        } catch (Exception e) {
            // 기타 예상하지 못한 모든 예외 처리
            throw new InvalidTokenException("토큰 처리 중 예기치 못한 오류가 발생했습니다.", e);
        }

    }

    /**
     * 주어진 JWT 토큰이 유효한지 검증하는 메서드
     * 토큰 유효성만 빠르게 확인하려고 만듬
     *
     * @param token 클라이언트로부터 받은 JWT 토큰 문자열
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token); // 파싱 + 서명 검증 + 만료 검증

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            // 유효하지 않은 토큰 → false 반환
            return false;
        }
    }

    /**
     * Refresh Token을 생성하는 메서드
     * payload는 memberId(PK) 정도로 최소화
     * Redis(In-Memory DB에 저장)해서 TTL(유효기간) 관리(자동으로 삭제)
     * @param expiredAt 토큰 유효기간(Duration)
     * @param memberVO 토큰에 담을 사용자 정보
     * @return 생성된 JWT 리프레시 토큰 문자열
     */
    public String generateRefreshToken(Duration expiredAt, MemberVO memberVO) {
        Date now = new Date(); // 토큰 발급 시간

        // 토큰 만료 시간 계산(현재시간 + expiredAt 밀리초)
        Date expiryDate = new Date(now.getTime() + expiredAt.toMillis());

        return Jwts.builder()
                .header()
                .add("typ", "JWT") // JWT 토큰임을 명시, "typ":"JWT"
                .and()
                .claims() // JWT 페이로드(claims) 설정 시작
                .add("sub", String.valueOf(memberVO.getId()))
                .add("iat", now.getTime() / 1000)  // iat(issued at): 토큰 발급 시각, 초 단위로 넣음
                .add("exp", expiryDate.getTime() / 1000)  // exp(expiration): 토큰 만료 시각, 초 단위, 이 시간 이후 토큰은 무효
                .and()
                .signWith(secretKey) // 서명(Signature) 설정
                .compact();  // 최종적으로 JWT 문자열 생성
//deprecated 경고 때문에 변경함
//        return Jwts.builder()
//                .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // JWT 헤더 타입
//                .setIssuer(jwtProperties.getIssuer())         // 발급자
//                .setIssuedAt(now)                             // 발급 시간
//                .setExpiration(expiryDate)                    // 만료 시간
//                .setSubject(String.valueOf(memberVO.getId())) // 사용자 식별자(memberId PK)
//                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명
//                .compact();
    }

}
