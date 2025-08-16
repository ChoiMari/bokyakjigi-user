package com.yakgurt.bokyakjigi.user.service;

import com.yakgurt.bokyakjigi.user.config.jwt.JwtProperties;
import com.yakgurt.bokyakjigi.user.domain.Member;
import com.yakgurt.bokyakjigi.user.domain.MemberRole;
import com.yakgurt.bokyakjigi.user.exception.InvalidRedisKeyException;
import com.yakgurt.bokyakjigi.user.exception.InvalidRefreshTokenException;
import com.yakgurt.bokyakjigi.user.exception.MissingRefreshTokenException;
import com.yakgurt.bokyakjigi.user.repository.MemberRepository;
import com.yakgurt.bokyakjigi.user.security.JwtProvider;
import com.yakgurt.bokyakjigi.user.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * refresh token 관련 redis 조회 및 access token 재발급 담당 서비스
 *
 * 역할 :
 * 클라이언트가 보낸 refresh token이 redis에 존재하는지 검증
 * 존재하면 새로은 Access Token 발급
 * 존재하지 않으면 잘못된 Refresh Token이거나, Refresh Token이 만료되었다는 의미이므로(TTL설정해둠), 예외 발생시켜서 프론트로 응답
 * 프론트에서는 이 예외 응답을 받아 재로그인 처리하면 됨
 */
@Service @Slf4j @RequiredArgsConstructor
public class RefreshTokenRedisService {
    private final MemberRepository memberRepository;
    private final JwtProperties jwtProperties;
    private final JwtProvider jwtProvider; // JWT 생성/검증 담당
    private final RedisTemplate<String, Object> redisTemplate; // redis에 refresh token저장되어있음 
    //-> 그걸 조회해서 있으면 새 access토큰을 발급해서 프론트에 응답으로 보내주면 됨 // 없으면 예외응답 ->프론트에서는 재로그인처리

    /**
     * Redis에서 Refresh Token 조회 확인(유효성 확인)하고 존재하면 새 Access Token을 생성해서 재발급
     * Redis에 Refresh Token이 존재 하면 유효,
     * 존재하지 않으면 위조이거나 Refresh Token이 만료되어 자동 삭제된 것(TTL 설정해 둠)
     * @param refreshToken 클라이언트에서 access token 재발급요청 dto로 보내진 refresh token
     * @return 새 Access Token
     */
    public String reissueAccessToken(String refreshToken){
        // 안전하게 한번 더 체크함(컨트롤러에 이미 검사있어서 필요없긴 함)
        if(refreshToken == null || refreshToken.trim().isEmpty() || refreshToken.trim().isBlank()){
            log.warn("[JWT] Refresh Token 예외발생 : 비어있거나 null입니다.");
            throw new MissingRefreshTokenException("Refresh Token은 필수입니다."); // 커스텀 예외 만들어서 던짐
        }
        //Redis에서 토큰 조회
         Object memberIdObj = redisTemplate.opsForValue().get(refreshToken);
        if(memberIdObj == null){ // redis에서 해당 refresh token이 존재하지 않으면 실행
            // 위조거나 만료이므로 예외 던짐 -> 프론트에서는 예외응답 받으면 재로그인 처리하면 됨
            log.warn("[Redis] Refresh Token이 조회되지 않음(만료거나 위조) : {}", refreshToken); //-> 조회되지 않는 토큰 로그로 찍는거라 보안상 문제 없다고 판단함
            //예외 던짐
            throw new InvalidRefreshTokenException("Refresh Token이 유효하지 않거나 만료되었습니다. 재로그인이 필요합니다.");
        }

        try{
            Long memberId = Long.valueOf(memberIdObj.toString());
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new InvalidRedisKeyException("해당 memberId 회원이 존재하지 않습니다."));

            // MemerVO 객체 생성
            MemberVO memberVO = new MemberVO(member.getId(),member.getEmail(),member.getNickname(), member.getRole().getRoleName());
            Duration accessTokenValidity = Duration.ofMinutes(jwtProperties.getAccessTokenExpirationMinutes()); // 토큰 만료시간
            //Access 토큰 새로 발급
            String accessToken = jwtProvider.generateAccessToken(accessTokenValidity, memberVO);
            return accessToken;
        } catch(NumberFormatException ex){
            log.warn("[Redis]memberId 타입 변환 예외발생 : {}", memberIdObj);
            throw new InvalidRedisKeyException("Redis에 저장된 memberId가 올바르지 않습니다.");
        }

    }
}
