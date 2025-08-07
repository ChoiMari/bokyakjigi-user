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
public class Member { //implements UserDetails 안하는 로직으로 변경(->SecurityUser에서 implements UserDetails)

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

    @Enumerated(EnumType.STRING) // enum 이름 자체를 문자열로 저장
    @Column(nullable = false, length = 1)
    private IsDeleted isDeleted;

    @Column(updatable = false)
    private LocalDateTime deletedAt;

    @Enumerated(EnumType.STRING)
    @Basic(optional = false)
    @Column(nullable = false, length = 20)
    private LoginType loginType;

    @Column(unique = true, length = 300)
    private String snsId;

    // 실제 Role 객체는 필요할 때만 DB에서 가져옴(지연로딩)
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 (Member는 Role 하나를 가짐)
    @JoinColumn(name = "ROLE_ID", nullable = false)  // FK 컬럼 이름, nn
    @Basic(optional = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // 양방향 연관관계 객체를 만들 때 서로 참조할 수 있도록 추가함
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)  // 1:1 관계로 프로필 이미지 매핑 // TODO : LAZY 로딩으로 인해 N+1 문제 발생할 수 있으므로 Fetch 전략 고려
    private ProfileImg profileImg;


    /**
     * 엔티티가 처음 저장되기 전에 실행되는 콜백 메서드
     * 생성일/수정일/탈퇴여부(N)/탈퇴일(null) 자동 저장
     */
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now(); // DB에 처음 저장할 때 현재 시간 넣기
        this.updatedAt = LocalDateTime.now(); // 처음 저장 시점과 같게 설정
        this.isDeleted = IsDeleted.N; // 삭제 여부는 처음에 'N' (삭제 안 됨)
        this.deletedAt = null;
    }

    /**
     * 엔티티가 수정 되기 전에 실행되는 콜백 메서드
     * 수정일을 현재 시간으로 갱신함
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now(); // 수정할 때마다 현재 시간으로 업데이트
    }


    /**
     * isDeleted 필드가 Y이면 탈퇴로 간주하여 false반환
     * @return 로그인 계정 활성화 상태
     */
    public boolean isActive() {
        return !"Y".equals(this.isDeleted); //  Y면 탈퇴 → false 반환, N면 활성 → true 반환
    }



//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        // Role 객체에서 권한명 꺼내서 SimpleGrantedAuthority 생성 후 리턴
//        return List.of(new SimpleGrantedAuthority(role.getRoleName().name()));
//        // List.of()로 권한을 하나만 가진 리스트로 감싸서 반환
//    } // TODO - Role 관리 다중 권한 고려(추후 다중 권한 부여가 필요하면 Set<Role> 등으로 변경 필요)
//
//
//    @Override
//    public String getUsername() {
//        return this.email;
//    }
//
//    @Override
//    public String getPassword() {
//        return this.password;
//    }
//
//
//    /**
//     * 계정이 만료되지 않았는지 여부 반환
//     * @return true 계정 만료 안됨(사용가능), false 계정 만료(사용 불가)
//     * TODO - 계정 만료 정책 반영 필요(계정 만료일자 컬럼 추가 후 만료 여부 로직 고려)
//     */
//    @Override
//    public boolean isAccountNonExpired() {
//        return true; // 현재는 만료 체크하지 않고 무조건 사용 가능으로 처리
//    }
//
//    /**
//     * 계정이 잠기지 않았는지 여부 반환
//     * @return true 계정 안 잠김(사용가능), false 계정 잠김(사용 불가)
//     */
//    @Override
//    public boolean isAccountNonLocked() {
//        return !"Y".equals(isDeleted); // 계정 탈퇴 여부, isDeleted가 "Y"면 탈퇴한 걸로 처리
//    }
//
//    /**
//     * 비밀번호가 만료되지 않았는지 여부 반환
//     * @return true 비밀번호 만료 안됨(사용 가능), false 비밀번호 만료(사용불가)
//     * TODO - 비밀번호 만료 정책 반영 필요
//     *  - 비밀번호 마지막 변경일 컬럼 추가 후 일정 기간 초과 시 false 반환 로직 구현 필요
//     */
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true; // 현재는 만료 체크하지 않고 무조건 사용가능 처리 TODO : 추후 90일 이상 변경 된적 없음 비밀번호 변경하라 로직 고려
//    }
//
//    /**
//     * 계정 활성화되어 있는지 여부 반환
//     * @return true 계정 활성화(사용가능) , false 계정 비활성화(사용불가)
//     * TODO - isDeleted 필드 외 상태 여부도 체크
//     *  - 여러 상태(예: 휴면계정, 정지 등) 추가 시 별도 상태 필드(또는 enum 사용) 고려
//     */
//    @Override
//    public boolean isEnabled() {
//        return !"Y".equals(isDeleted);  // isDeleted가 "Y"면 비활성화 처리
//    }





}
