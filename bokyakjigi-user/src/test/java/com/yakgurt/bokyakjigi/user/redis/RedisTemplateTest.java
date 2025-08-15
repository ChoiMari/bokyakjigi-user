package com.yakgurt.bokyakjigi.user.redis;

import com.yakgurt.bokyakjigi.user.config.jwt.JwtProperties;
import com.yakgurt.bokyakjigi.user.domain.Member;
import com.yakgurt.bokyakjigi.user.repository.MemberRepository;
import com.yakgurt.bokyakjigi.user.security.JwtProvider;
import com.yakgurt.bokyakjigi.user.vo.MemberVO;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class RedisTemplateTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private MemberRepository memberRepo;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private JwtProperties jwtProperties;

   // @Test // 의존성 주입 잘 되는지 확인
    public void testDependencyInjection(){
        assertThat(redisTemplate).isNotNull(); //redisTemplate null이 아님 주장.
        // true면 의존성 주입 잘 받음(테스트 성공), false면 null(의존성 주입 안됨, test실패)
        log.info(redisTemplate.toString());
        assertThat(memberRepo).isNotNull();
        assertThat(jwtProvider).isNotNull();
        assertThat(jwtProperties).isNotNull();

        log.info(memberRepo.toString());
        log.info(jwtProvider.toString());
        log.info(jwtProperties.toString());
    }

    @Test
    @Transactional // 테스트 실행 동안 영속성 컨텍스트가 유지되므로 Lazy 로딩이 가능
    public void testRefreshTokenCRUD() {

        Member  member = memberRepo.findByEmail("test@example.com")
                .orElseThrow(() -> new RuntimeException("해당 email로 가입된 회원이 없습니다."));
                // .orElseThrow() : Optional객체에서 값이 없을 때 예외를 던지는 메서드
        MemberVO memberVO = new MemberVO(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getRole().getRoleName()
        );

        // Refresh Token 유효기간 설정
        Duration refreshTokenValidity = Duration.ofDays(jwtProperties.getRefreshTokenExpirationDays());

        // 실제 JwtProvider로 Refresh Token 생성
        String refreshToken = jwtProvider.generateRefreshToken(refreshTokenValidity, memberVO);

        String key = "RT:" + member.getId();

        // 저장
        redisTemplate.opsForValue().set(key, refreshToken, refreshTokenValidity);

        //값이 제대로 저장됐는지 검증
        String savedToken = (String) redisTemplate.opsForValue().get(key);
        //  Redis에 저장된 Refresh Token 값이 방금 생성한 값과 동일한지 확인
        assertThat(savedToken).isEqualTo(refreshToken); // 동일하면 저장 성공
        log.info("저장된 refresh token: {}", savedToken);

        //TTL(만료시간)이 제대로 설정됐는지 검증
        Long expireSeconds = redisTemplate.getExpire(key); // getExpire(key): 남은 TTL(초 단위)을 반환
        assertThat(expireSeconds).isGreaterThan(0); // 0 : TTL이 설정되어 있고 아직 만료되지 않음
        assertThat(expireSeconds).isLessThanOrEqualTo(refreshTokenValidity.getSeconds());
        // refreshTokenValidity.getSeconds() : 설정한 만료 시간보다 크면 안 됨
        log.info("키 '{}'의 TTL(초 단위): {}", key, expireSeconds);

        // 조회
        String storedToken = (String) redisTemplate.opsForValue().get(key);
        log.info("Redis 조회 - key: {}, value: {}", key, storedToken); // 조회 로그
        assertThat(storedToken).isEqualTo(refreshToken);

        // 삭제
        boolean deleted = Boolean.TRUE.equals(redisTemplate.delete(key));
        log.info("Redis 삭제 - key: {}, 삭제 성공 여부: {}", key, deleted); // 삭제 로그

        String afterDelete = (String) redisTemplate.opsForValue().get(key);
        log.info("삭제 후 조회 - key: {}, value: {}", key, afterDelete); // 삭제 후 조회 로그
        assertThat(afterDelete).isNull();

//        // 삭제
//        redisTemplate.delete(key);
//        assertThat(redisTemplate.opsForValue().get(key)).isNull();
    }

}
