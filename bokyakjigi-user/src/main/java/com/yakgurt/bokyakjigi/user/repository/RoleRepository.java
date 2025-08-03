package com.yakgurt.bokyakjigi.user.repository;

import com.yakgurt.bokyakjigi.user.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
