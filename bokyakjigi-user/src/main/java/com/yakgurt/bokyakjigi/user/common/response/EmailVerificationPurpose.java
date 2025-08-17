package com.yakgurt.bokyakjigi.user.common.response;

/**
 * 이메일 인증 발송용 용도 구분 위해 정의함
 */
public enum EmailVerificationPurpose {
    SIGNUP,          // 회원가입
    PASSWORD_RESET,  // 비밀번호 초기화
    FIND_ID,         // 아이디 찾기
    MYPAGE           // 마이페이지 민감 정보 접근
}
