package com.sb_ecom.backend_ecom.repo;

import com.sb_ecom.backend_ecom.model.AppRole;
import com.sb_ecom.backend_ecom.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(AppRole appRole);
}
