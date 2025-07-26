package com.yakgurt.bokyakjigi.user.vo;

import com.yakgurt.bokyakjigi.user.domain.MemberRole;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Member 엔티티에서 JWT 토큰 등에 사용할 최소한의 사용자 정보만 담은 VO 클래스
 */
@Getter
@Builder
@ToString(exclude = "email") // 민감정보 제거
@EqualsAndHashCode(of = {"id", "email"}) // 특정 필드만 비교
public class MemberVO implements Serializable {
    private static final long serialVersionUID = 1L; // JWT나 Redis에 이 VO를 저장하거나 전달할 일 있으면 필요함

    // 여기에 선언한 필드들이 JWT토큰의 페이로드(클레임즈)에 들어가는 데이터
    private final Long id;
    private final String email; //로그인 식별자로 사용 -> 서비스에 따라서 넣지말지 결정함(민감서비스에는 넣지 말 것), 개인정보 이슈있을땐 그냥 id로 해결
    private final String nickname;
    private final MemberRole role; // 단일 권한으로 설정해놓음(추후 버전업한다면 다중권한으로 바꾸기-> 테이블 설계도 변경해야함)
    
    //참고) @JsonIgnore 쓰면 json으로 직렬화할 때 일부 필드 숨길 수 있음
}
