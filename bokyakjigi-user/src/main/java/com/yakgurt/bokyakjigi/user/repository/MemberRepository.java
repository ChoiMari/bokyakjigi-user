package com.yakgurt.bokyakjigi.user.repository;

import com.yakgurt.bokyakjigi.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    
    // 이메일로 회원 조회 (로그인용)
    Optional<Member> findByEmail(String email);

    // 닉네임 중복 체크용
    boolean existsByNickname(String nickname);

    // 이메일 중복 체크용
    boolean existsByEmail(String email);

    // SNS ID로 조회 (소셜 로그인용)
    Optional<Member> findBySnsId(String snsId);

    // 탈퇴 안 한 활성 사용자만 조회
    Optional<Member> findByEmailAndIsDeleted(String email, String isDeleted);
}
