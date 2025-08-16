package com.yakgurt.bokyakjigi.user.service;

import com.yakgurt.bokyakjigi.user.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor @Slf4j
public class SignOutService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtProvider jwtProvider;

    /**
     * 로그아웃
     * refresh token에서 memberId(redis의 key)를 추출해서 redis에 refresh token을 삭제
     * @param refreshToken 프론트에서 요청dto로 받은 refresh token
     */
    public boolean signOut(String refreshToken){
         // refresh token에서 memberId를 추출
         Long memberId = jwtProvider.getMemberIdFromRefreshToken(refreshToken);

         //redis 키 생성
        String redisKey = "RT:" + memberId;

        // redis에서 Refresh Token 삭제
        Boolean deleted = redisTemplate.delete(redisKey); //-> true : 삭제, false : (키가 존재하지 않아서) 삭제할 게 없음 // null 가능성 있음
        if (Boolean.TRUE.equals(deleted)) {
            // 삭제 성공
            log.info("로그아웃 성공 - memberId={}, Redis Key deleted", memberId);
        } else {
            // 삭제 실패 또는 key가 없었음
            log.warn("삭제할 키 없음 또는 실패 - memberId={}", memberId);
            // 키가 없는 경우 : 이미 만료되서 redis에서 삭제된 경우, 사용자가 이미 로그아웃 한 경우, 프론트에서 잘못된 refresh token을 보낸경우 등
        }
        return Boolean.TRUE.equals(deleted);
    }
}
