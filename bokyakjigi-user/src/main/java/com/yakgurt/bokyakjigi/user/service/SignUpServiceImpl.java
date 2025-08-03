package com.yakgurt.bokyakjigi.user.service;

import com.yakgurt.bokyakjigi.user.dto.SignUpRequestDto;
import com.yakgurt.bokyakjigi.user.repository.MemberRepository;
import com.yakgurt.bokyakjigi.user.repository.ProfileImgRepository;
import com.yakgurt.bokyakjigi.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SignUpServiceImpl implements SignUpService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final ProfileImgRepository profileImgRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PROFILE_URL = "/images/default-profile.png";

    @Override
    @Transactional // 모두 성공해야 commit처리, 하나의 트랜잭션으로 묶음
    public Long signUp(SignUpRequestDto dto) {
        // 이메일 중복검사(중복이면 예외)

        // 닉네임 중복 검사(중복이면 예외)

        // ROLE_USER 조회(없으면 예외 발생)

        // 비밀번호 암호화

        // Member 엔터티 생성

        // 저장(DB에 insert)

        // 기본 프로필 이미지 엔터티 생성 및 저장(DB에 insert)

        return 0L;
    }
}
