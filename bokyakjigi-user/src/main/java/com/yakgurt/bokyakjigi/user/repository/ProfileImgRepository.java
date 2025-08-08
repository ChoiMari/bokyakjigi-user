package com.yakgurt.bokyakjigi.user.repository;

import com.yakgurt.bokyakjigi.user.domain.ProfileImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileImgRepository extends JpaRepository<ProfileImg, Long> {
    Optional<ProfileImg> findByMember_Id(Long memberId);

}
