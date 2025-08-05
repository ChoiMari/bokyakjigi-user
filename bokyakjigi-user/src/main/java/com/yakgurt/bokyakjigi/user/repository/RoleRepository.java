package com.yakgurt.bokyakjigi.user.repository;

import com.yakgurt.bokyakjigi.user.domain.Member;
import com.yakgurt.bokyakjigi.user.domain.MemberRole;
import com.yakgurt.bokyakjigi.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(MemberRole role);
}
