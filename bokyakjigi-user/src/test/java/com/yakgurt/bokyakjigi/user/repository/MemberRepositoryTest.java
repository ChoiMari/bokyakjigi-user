package com.yakgurt.bokyakjigi.user.repository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yakgurt.bokyakjigi.user.domain.Member;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.AssertTrue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //@Test
    public void testDependencyInjection(){
        // @Autowired로 주입 잘 받았는지 test
        assertThat(memberRepo).isNotNull(); //memberRepo가 null이 아님 주장. true면 의존성 주입 잘 받음(테스트 성공), false면 null(의존성 주입 안됨, test실패)
        log.info(memberRepo.toString());
        assertThat(passwordEncoder).isNotNull();
        log.info(passwordEncoder.toString());
    }

    //@Test
    //@Transactional 붙어야 연관된 role 엔티티도 지연로딩 가능.
    public void findById(){
        Member test1 = memberRepo.findById(1L).get(); //-> 값이 있으면 값을 리턴, 없으면 예외 던짐
        log.info("{},{}",test1,test1.getRole());
    }

    @Test
    @Transactional
    public void findByEmail(){
        Member test2 = memberRepo.findByEmail("test@example.com").get();
        log.info("{},{}",test2,test2.getRole());
    }



}
