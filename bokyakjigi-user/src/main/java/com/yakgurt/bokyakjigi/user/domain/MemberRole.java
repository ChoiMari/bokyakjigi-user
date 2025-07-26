package com.yakgurt.bokyakjigi.user.domain;

/**
 * 사용자 권한을 정의하는 enum
 * 각 사용자의 권한은 ADMIN, USER, GUEST 중 1개의 권한을 가질 수 있음(단일 권한)
 * 추후 버전업시 다중 권한 고려.
 */
public enum MemberRole {
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER"),
    GUEST("ROLE_GUEST");

    // enum 상수에 매핑되는 실제 권한 문자열
    private String authority;

    MemberRole(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }
}
