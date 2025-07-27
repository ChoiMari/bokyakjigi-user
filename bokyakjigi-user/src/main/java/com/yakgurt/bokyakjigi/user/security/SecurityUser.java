package com.yakgurt.bokyakjigi.user.security;

import com.yakgurt.bokyakjigi.user.domain.Member;
import com.yakgurt.bokyakjigi.user.domain.MemberRole;
import com.yakgurt.bokyakjigi.user.vo.MemberVO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** 변경 전 주석
 * 스프링 시큐리티에서 사용할 사용자 정보 래퍼 클래스(implements UserDetails)
 * MemberVO를 기반으로 인증/인가 정보 제공(JWT 필터에서 시큐리티 컨텍스트에 인증 객체 저장할 때 사용함)
 * 시큐리티 프레임워크 내부가 UserDetails 인터페이스를 호출
 * 도메인 모델 데이터를 기반으로 시큐리티가 요구하는 형식과 역할만 수행
 *
 * 사용 용도
 * 1. 스프링 시큐리티가 인증/인가 과정에서 사용하는 사용자 정보 래퍼 클래스
 * 2. UserDetailsService 구현체가 DB에서 사용자 조회 후 반환하는 객체
 * 3. JWT 토큰 검증 후 인증 객체(Authentication) 생성 시 내부 정보로 사용됨
 * 4. SecurityContext에 저장되어 권한 검사, 활성 상태 체크 등에 활용됨
 * 5. 컨트롤러에서 @AuthenticationPrincipal로 주입 받아 로그인 사용자 정보 활용 가능
 *
 * 패스워드 null 반환 관련:
 * JWT 인증방식에서는 패스워드를 직접 사용하지 않음(토큰 기반 인증)
 * 따라서 getPassword()가 null이어도 인증에 문제 없음
 * -> 스프링 시큐리티 폼 로그인(form login) 안쓸거면 상관없다
 */

/** 변경 후 주석
 * JWT 토큰 클레임에 담긴 최소한의 사용자 정보만 보유하는 SecurityUser
 * UserDetails 구현체
 * DB 조회 없이 토큰 정보만으로 인증/인가 처리 목적
 */
@RequiredArgsConstructor
@Getter
public class SecurityUser implements UserDetails {
    private final Long id;          // 토큰 클레임에 들어갈 유저 식별자
    private final String email;     // 로그인 식별자(주로 이메일)
    private final String nickname;  // 필요 시 닉네임도 포함
    private final MemberRole role;  // 권한 정보

    public SecurityUser(MemberVO memberVO){
        this.id = memberVO.getId();
        this.email = memberVO.getEmail();
        this.nickname = memberVO.getNickname();
        this.role = memberVO.getRole();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한명(Role enum 이름)을 SimpleGrantedAuthority로 감싸서 반환
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email; // 로그인 식별자로 이메일 반환
    }

    @Override
    public String getPassword() {
        return null; // JWT 토큰 인증이므로 비밀번호 없음
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 토큰 기반 인증에서는 만료 로직 별도 구현 필요 시 추가
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 토큰 기반 인증에서 DB 조회 없으면 잠금 체크 불가 -> 기본 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호 만료 체크 없음
    }

    @Override
    public boolean isEnabled() {
        return true; // 활성화 체크 DB 조회 없으면 기본 true, 필요 시 DB 조회 후 판단
    }

    // 변경 전
//    private final MemberVO memberVO; // JWT 클레임즈에 담긴 최소한 필드 DTO
//    private final Member member; // DB에서 조회한 도메인 모델(풀필드)
//    //-> 주의 할 점
//    /*
//    * member가 null일 가능성
//    *    - 클레임 기반 인증이라면 토큰에서 복원한 memberVO는 있는데 member는 상황에 따라 없을 수도 있다
//    * memberVO와 member 간 데이터 불일치 위험성
//    *   - 클레임은 토큰 발행 시점 데이터라 DB 최신 상태와 다를 수 있기 때문에
//    *
//    * => SecurityUser 생성 시점에 둘 다 정확히 세팅해야 함(동기화, 데이터 최신화)
//    * */
//
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        // Role 객체에서 권한명 꺼내서 SimpleGrantedAuthority 생성 후 리턴
//        return List.of(new SimpleGrantedAuthority(memberVO.getRole().name()));
//
//        // List.of()로 권한을 하나만 가진 리스트로 감싸서 반환
//    } // TODO - Role 관리 다중 권한 고려(추후 다중 권한 부여가 필요하면 Set<Role> 등으로 변경 필요)
//
//
//    @Override
//    public String getUsername() {
//        return memberVO.getEmail();
//    }
//
//    @Override
//    public String getPassword() {
//        return null; // JWT 기반 인증이므로 비밀번호는 필요 없음
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
//        return member != null && member.isActive(); // member가 null이 아님 + 탈퇴이면 false반환
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
//        return member.isActive(); // 활성(true)면 사용 가능, 비활성(false)면 사용 불가
//    }
}
