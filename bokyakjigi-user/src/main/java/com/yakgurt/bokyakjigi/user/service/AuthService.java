package com.yakgurt.bokyakjigi.user.service;

import com.yakgurt.bokyakjigi.user.config.jwt.JwtProperties;
import com.yakgurt.bokyakjigi.user.domain.Member;
import com.yakgurt.bokyakjigi.user.repository.MemberRepository;
import com.yakgurt.bokyakjigi.user.security.JwtProvider;
import com.yakgurt.bokyakjigi.user.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

/**
 * 인증 관련 비즈니스 로직 처리 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder pwdEncoder;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 로그인 처리 메서드
     * @param email 사용자 이메일 (로그인 ID)
     * @param rawPassword 입력된 비밀번호 (평문)
     * @return 유효한 JWT 액세스 토큰 문자열
     * @throws UsernameNotFoundException 회원이 존재하지 않을 경우
     * @throws BadCredentialsException 비밀번호 불일치 시
     * 전역 예외 처리 클래스에서 json형태로 보내야함 -> 작업완료
     */
    @Transactional(readOnly = true)  // 읽기 전용 트랜잭션 (성능 최적화용)
    public String signIn(String email, String rawPassword) {
        log.info("signIn start (email={})",email);
        // 이메일과 탈퇴 여부 'N'로 활성 회원 조회
        Member member = memberRepository.findByEmailAndIsDeleted(email, "N") //email이 일치하고, isDeleted가 N인 MEMBER객체를 찾음
                .orElseThrow(() -> new UsernameNotFoundException("가입된 회원이 없습니다."));
        //해당 값이 없을 경우 UsernameNotFoundException예외 발생

        // 비밀번호 검증
        if (!pwdEncoder.matches(rawPassword, member.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        MemberVO memberVO = new MemberVO(member.getId(), member.getEmail(), member.getNickname(), member.getRole().getRoleName()); // 필요한 필드만
        // 엑세스 토큰
        Duration accessTokenValidity = Duration.ofMinutes(jwtProperties.getAccessTokenExpirationMinutes()); // 토큰 만료시간
        String token = jwtProvider.generateAccessToken(accessTokenValidity, memberVO);

        //Refresh Token 유효기간
        Duration refreshTokenValidity = Duration.ofDays(jwtProperties.getRefreshTokenExpirationDays());
        // Duration : java 8 이상에서 제공하는 시간 단위 다루는 클래스. jwt 만료시간 설정 위해 사용
        // jwtProperties.getAccessTokenExpirationMinutes() :  application.yml 혹은 properties에서 설정한 리프레시 토큰 만료일(숫자) 가져오기(14일로 설정해둠)
        String refreshToken = jwtProvider.generateRefreshToken(refreshTokenValidity, memberVO);

        //Redis에 저장 (Key: "RT:{memberId}", Value: refreshToken, TTL: 14일)
        String redisKey = "RT:" + member.getId(); // RT : RefreshToken 약어
        redisTemplate.opsForValue().set(redisKey, refreshToken, refreshTokenValidity);

        log.info("signIn success - email={}", email);
        return token;
    }
}
