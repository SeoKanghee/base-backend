package com.kelly.base.product.identity.repository;

import com.kelly.base.product.identity.domain.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * role table repository
 *
 * @author 서강희
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    /**
     * Role code로 Role 조회
     *
     * @param code role code
     * @return Role entity
     */
    Optional<Role> findByCode(String code);
}
