package com.yakgurt.bokyakjigi.user.domain;

import java.time.LocalDateTime;
import java.util.*;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor // 기본 생성자 자동 생성
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 모든 필드 값 파라미터로 받는 생성자 생성, 접근 제한자는 private
@Builder // 빌더 패턴 사용
@Getter
@ToString(callSuper = true) // toString() 자동 생성, 부모 클래스 필드도 포함
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false) // equals()와 hashCode() 자동 생성, 명시한 필드만 포함, 부모 클래스 필드 제외
@Entity // JPA 엔티티임을 명시, DB 테이블과 매핑됨
@Table(name = "MEMBERS") // 엔티티가 매핑될 테이블 이름 지정
public class Member implements UserDetails {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 컬럼
    private Long id;

    @Column(nullable = false, unique = true, length = 200, updatable = false) // DB 컬럼 제약조건: nn,uk, 길이 200, update 불가
    @NaturalId  // Hibernate가 비즈니스 식별자로 인식
    @EqualsAndHashCode.Include // Lombok equals/hashCode에 포함
    @Basic(optional = false) // JPA가 null 불가로 인식
    private String email;

    @Column(nullable = false, length = 1000) // DB 컬럼 제약조건: nn, 길이 1000
    @Basic(optional = false) // JPA가 null 불가로 인식
    private String password;

    @Column(nullable = false, unique = true, length = 100) // DK : nn, uk, 길이 100
    @Basic(optional = false)
    private String nickname;

    @Basic(optional = false)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Basic(optional = false)
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, length = 1)
    private String isDeleted;

    @Column(updatable = false)
    private LocalDateTime deletedAt;

    @Basic(optional = false)
    @Column(nullable = false, length = 20)
    private String loginType;

    @Column(unique = true, length = 300)
    private String snsId;

    // 실제 Role 객체는 필요할 때만 DB에서 가져옴(지연로딩)
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 (Member는 Role 하나를 가짐)
    @JoinColumn(name = "ROLE_ID", nullable = false)  // FK 컬럼 이름, nn
    @Basic(optional = false)
    private Role role;

    // 양방향 연관관계 객체를 만들 때 서로 참조할 수 있도록 추가함
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)  // 1:1 관계로 프로필 이미지 매핑
    private ProfileImg profileImg;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Role 객체에서 권한명 꺼내서 SimpleGrantedAuthority 생성 후 리턴
        return List.of(new SimpleGrantedAuthority(role.getRoleName()));
        // List.of()로 권한을 하나만 가진 리스트로 감싸서 반환
    }

    @Override
    public String getUsername() {
        return this.email;
    }

}
