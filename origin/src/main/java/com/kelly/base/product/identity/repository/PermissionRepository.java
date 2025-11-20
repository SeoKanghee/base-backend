package com.kelly.base.product.identity.repository;

import com.kelly.base.product.identity.domain.permission.Permission;
import com.kelly.base.product.identity.repository.custom.PermissionRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * permission table repository
 *
 * @author 서강희
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long>, PermissionRepositoryCustom {
}
