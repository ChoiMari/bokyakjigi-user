package com.yakgurt.bokyakjigi.user.domain;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 모든 필드 값 파라미터로 받는 생성자 생성, 접근 제한자는 private
@Builder // 빌더 패턴 사용
@Getter
@ToString(callSuper = true) // toString() 자동 생성, 부모 클래스 필드도 포함
@Entity // JPA 엔티티임을 명시, DB 테이블과 매핑됨
@Table(name = "APP_ROLE") // 엔티티가 매핑될 테이블 이름 지정
public class Role {
    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROLE_NAME", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private MemberRole roleName;
}
