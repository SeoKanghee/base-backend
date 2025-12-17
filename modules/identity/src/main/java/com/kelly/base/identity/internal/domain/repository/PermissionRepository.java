package com.kelly.base.identity.internal.domain.repository;

import com.kelly.base.identity.internal.domain.Permission;
import com.kelly.base.identity.internal.domain.repository.custom.PermissionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * permission table repository
 *
 * @author 서강희
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, PermissionRepositoryCustom {
}
