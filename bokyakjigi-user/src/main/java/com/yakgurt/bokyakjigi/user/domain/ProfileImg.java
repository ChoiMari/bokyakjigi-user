package com.yakgurt.bokyakjigi.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@ToString(exclude = "member")
@Entity
@Table(name = "PROFILE_IMG")
public class ProfileImg { // 프로필 이미지 정보를 저장하는 엔티티
    @Id //PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // Member와 1:1
    @JoinColumn(name = "MEMBER_ID", nullable = false, unique = true)
    @Basic(optional = false)
    private Member member;

    @Column(name = "IMG_URL", nullable = false, length = 3000)
    @Basic(optional = false)
    private String imgUrl;

    @Column(name = "UPLOADED_AT", nullable = false)
    @Basic(optional = false)
    private LocalDateTime uploadedAt;
}
