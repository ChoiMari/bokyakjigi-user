package com.yakgurt.bokyakjigi.user.web;

import com.yakgurt.bokyakjigi.user.dto.SignUpEmailVerificationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 관련 이메일 인증을 담당하는 컨트롤러
 * - 회원 가입 후 이메일 인증 처리
 * - 마이페이지의 민감 정보 접근 전 이메일 인증 처리
 * (하기 이전에는 ***마스킹 처리함-> 인증 후 전체 정보확인 + 회원정보 수정 가능. 그걸 위한 인증처리)
 * - 아이디/ 비밀번호 찾기에 필요한 이메일 인증 처리(휴대폰 인증은 사업자등록증 있어야 해서 이메일로만 진행)
 * - 이메일 인증 재전송 기능
 * [참고]
 * 회원가입/아이디/비밀번호 찾기 인증은 로그인 안해도 접근 가능하도록 /api/auth로 시작 (시큐리티 필터 처리에서 허용해둠)
 * 마이페이지는 로그인이 필요하므로 /api/auth로 시작하면 안됨
 */
@RestController @Slf4j @RequiredArgsConstructor @RequestMapping("/api")
public class MemberEmailVerificationController {

//    @PostMapping("/auth/signup/email-verify")
//    public ResponseEntity<SignUpEmailVerificationResponseDto>
}
