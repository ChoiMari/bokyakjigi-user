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
}
