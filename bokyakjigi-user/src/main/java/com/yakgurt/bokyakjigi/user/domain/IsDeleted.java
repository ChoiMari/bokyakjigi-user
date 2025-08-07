package com.yakgurt.bokyakjigi.user.domain;

/**
 * 탈퇴 여부탈퇴 여부 (회원 상태 구분용)
 */
public enum IsDeleted {
    Y("탈퇴"), //public static final IsDeleted Y = new IsDeleted("탈퇴");와 같음
    N("정상"); // public static final IsDeleted N= new IsDeleted("정상");과 같음

    private final String description;

    IsDeleted(String description) { //
        this.description = description;
    }

    //getter
    public String getDescription() {
        return this.description;
    }
}
