package com.yakgurt.bokyakjigi.user.service;

import com.yakgurt.bokyakjigi.user.domain.Member;
import com.yakgurt.bokyakjigi.user.domain.MemberRole;
import com.yakgurt.bokyakjigi.user.domain.ProfileImg;
import com.yakgurt.bokyakjigi.user.dto.SignUpRequestDto;
import com.yakgurt.bokyakjigi.user.exception.RoleNotFoundException;
import com.yakgurt.bokyakjigi.user.repository.MemberRepository;
import com.yakgurt.bokyakjigi.user.repository.ProfileImgRepository;
import com.yakgurt.bokyakjigi.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.yakgurt.bokyakjigi.user.exception.DuplicateNicknameException;
import com.yakgurt.bokyakjigi.user.exception.DuplicateEmailException;
import com.yakgurt.bokyakjigi.user.domain.Role;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpServiceImpl implements SignUpService {

    private final MemberRepository memberRepo;
    private final RoleRepository roleRepo;
    private final ProfileImgRepository profileImgRepo;
    private final PasswordEncoder pwdEncoder;
    private static final String DEFAULT_PROFILE_URL = "/images/default-profile.png";

    /**
     * 회원가입 처리 메서드
     * @param dto 회원가입 요청 DTO
     * @return 가입 완료된 회원의 id(고유 식별자, PK)
     */
    @Override
    @Transactional // 모두 성공해야 commit처리, 하나의 트랜잭션으로 묶음
    public Long signUp(SignUpRequestDto dto) {
        log.debug("signUp(email={},nickname={})",dto.getEmail(),dto.getNickname()); //-> 비밀번호 절대 찍으면 안됨
        //-> 이메일은 회사마다 정책에 따라서 로그 찍는 여부 다르다고 함

        // 이메일 중복검사(중복이면 DuplicateEmailException 예외)
        checkDuplicateEmail(dto.getEmail());

        // 닉네임 중복 검사(중복이면 DuplicateNicknameException 예외)
        checkDuplicateNickname(dto.getNickname());

        // ROLE_USER 조회(없으면 예외 발생)
        Role roleUser = roleRepo.findByRoleName(MemberRole.USER)
                .orElseThrow(() -> new RoleNotFoundException("USER 권한이 없습니다.")); // 전역예외처리기에서 처리

        // 비밀번호 암호화
        String encodedPassword = pwdEncoder.encode(dto.getPassword());

        // Member 엔터티 생성
        Member member = dto.toEntity(encodedPassword, roleUser);

        // 저장(DB에 insert)
        Member savedMember = memberRepo.save(member);

        // 가입한 회원정보로 프로필 이미지 생성
        ProfileImg profileImg = ProfileImg.builder()
                .member(savedMember)  // member 객체를 넣으면 JPA가 FK인 MEMBER_ID 컬럼에 member.getId() 저장
                .imgUrl(DEFAULT_PROFILE_URL)
                .uploadedAt(LocalDateTime.now())
                .build();

        //저장(DB에 insert)
        profileImgRepo.save(profileImg);

        return savedMember.getId(); // 가입한 회원 PK 반환
    }

    /**
     * 이메일 중복 검사 메서드
     * @param email 검사 대상 이메일
     * @throws DuplicateEmailException 이메일이 이미 DB에 존재하면(중복이면) 사용할 수 없으므로 예외 던짐.
     * 리턴타입 void : 호출하는 쪽에서는 이 메서드 호출 후 예외 발생하지 않으면 중복 없음으로 판단.
     */
    @Override
    public void checkDuplicateEmail(String email) {
        log.info("checkDuplicateEmail(email={})", email);
        if(memberRepo.existsByEmail(email)) { // 중복이 있으면 실행됨(사용불가 email)
            // 커스텀 예외 생성해서 던짐 -> 전역 예외 처리기(@ControllerAdvice) 즉, GlobalExceptionHandler 클래스에서 받아서 처리함
            log.warn("중복된 이메일 {}", email);
            throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
        }
    }

    /**
     * 닉네임 중복 검사 메서드
     * @param nickname 검사할 닉네임
     * @throws DuplicateNicknameException 닉네임이 이미 DB에 존재하는게 있으면 사용할 수 없으므로 예외 던짐.
     * 리턴타입 void : 호출하는 쪽에서는 이 메서드 호출 후 예외 발생하지 않으면 중복 없음으로 판단
     */
    @Override
    public void checkDuplicateNickname(String nickname) {
        log.info("checkDuplicateNickname(nickname={})",nickname);
        if(memberRepo.existsByNickname(nickname)){ // 중복 있으면 실행됨(사용불가 nickname)
            throw new DuplicateNicknameException("이미 사용 중인 닉네임입니다."); // 커스텀 예외 생성해서 던짐 -> 전역 예외처리기에서 받아서 처리
        
        }
    }

}
