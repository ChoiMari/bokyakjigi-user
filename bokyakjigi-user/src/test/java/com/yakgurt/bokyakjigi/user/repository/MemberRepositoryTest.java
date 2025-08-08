package com.yakgurt.bokyakjigi.user.repository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.yakgurt.bokyakjigi.user.domain.Member;
import com.yakgurt.bokyakjigi.user.domain.MemberRole;
import com.yakgurt.bokyakjigi.user.domain.ProfileImg;
import com.yakgurt.bokyakjigi.user.domain.Role;
import com.yakgurt.bokyakjigi.user.dto.SignUpRequestDto;
import com.yakgurt.bokyakjigi.user.exception.RoleNotFoundException;
import com.yakgurt.bokyakjigi.user.service.SignUpService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.constraints.AssertTrue;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Slf4j
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SignUpService memberSvc;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private ProfileImgRepository profileImgRepo;

    private SignUpRequestDto dto;

    @BeforeEach
    void setUp() {
        dto = SignUpRequestDto.builder()
                .email("testSave@test.com")
                .nickname("후후1234")
                .password("t12345678$")
                .build();
    }

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
    public void testFindById(){
        Member test1 = memberRepo.findById(1L).get(); //-> 값이 있으면 값을 리턴, 없으면 예외 던짐
        log.info("{},{}",test1,test1.getRole());
    }

   // @Test
    //@Transactional
    public void testFindByEmail(){
        Member test2 = memberRepo.findByEmail("test@example.com").get();
        log.info("{},{}",test2,test2.getRole());
        String rawPassword = "c12345678#";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("바밀번호 암호화: " + encodedPassword);
    }

    //@Test
    //@Transactional
   // @Rollback(false) // 실제 DB 반영 확인용, 기본은 true(롤백)
    public void testSave(){

        // 이메일 중복검사(중복이면 DuplicateEmailException 예외)
        memberSvc.checkDuplicateEmail("testSave@test.com");

        // 닉네임 중복 검사(중복이면 DuplicateNicknameException 예외)
        memberSvc.checkDuplicateNickname("후후1234");

        // ROLE_USER 조회(없으면 예외 발생)
        Role roleUser = roleRepo.findByRoleName(MemberRole.USER)
                .orElseThrow(() -> new RoleNotFoundException("USER 권한이 없습니다.")); // 전역예외처리기에서 처리

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode("t12345678$");

        // Member 엔터티 생성
        Member member = dto.toEntity(encodedPassword, roleUser);
        log.info("member = {}",member);

        // 저장(DB에 insert)
        Member savedMember = memberRepo.save(member);

        // 가입한 회원정보로 프로필 이미지 생성
        ProfileImg profileImg = ProfileImg.builder()
                .member(savedMember)  // member 객체를 넣으면 JPA가 FK인 MEMBER_ID 컬럼에 member.getId() 저장
                .imgUrl("/images/default-profile.png")
                .uploadedAt(LocalDateTime.now())
                .build();

        //저장(DB에 insert)
        profileImgRepo.save(profileImg);

        // 검증
        // MemberRepository에서 해당 이메일로 가입한 회원이 존재하는지 확인
        assertTrue(memberRepo.findByEmail("testSave@test.com").isPresent());
        // 저장된 프로필 이미지가 존재함을 주장
        assertTrue(profileImgRepo.findByMember_Id(savedMember.getId()).isPresent());

    }


}
