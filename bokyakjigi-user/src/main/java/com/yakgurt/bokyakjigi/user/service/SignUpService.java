package com.yakgurt.bokyakjigi.user.service;

import com.yakgurt.bokyakjigi.user.dto.SignUpRequestDto;

/**
 * 회원가입 서비스 인터페이스
 */
public interface SignUpService {
    /**
     * 회원가입 처리
     * @param dto 회원가입 요청 DTO
     * @return 가입된 회원의 ID(PK)
     */
    Long signUp(SignUpRequestDto dto);

    /**
     * 이메일 중복 체크
     * @param email 검사할 이메일
     */
    void checkDuplicateEmail(String email);

    /**
     * 닉네임 중복체크
     * @param nickname 검사할 닉네임
     */
    void checkDuplicateNickname(String nickname);
}
