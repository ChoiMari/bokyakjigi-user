package com.yakgurt.bokyakjigi.user.domain;

/**
 * 일반회원과 SNS 회원 구분용
 */
public enum LoginType {
    LOCAL("일반가입"),
    GOOGLE("구글"),
    KAKAO("카카오"),
    NAVER("네이버");

    private String description;

    LoginType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
