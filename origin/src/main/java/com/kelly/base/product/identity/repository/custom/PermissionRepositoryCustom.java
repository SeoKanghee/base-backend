package com.kelly.base.product.identity.repository.custom;

import com.kelly.base.product.identity.domain.permission.Permission;

import java.util.Set;

/**
 * permission table repository (custom)
 *
 * @author 서강희
 */
public interface PermissionRepositoryCustom {
    /**
     * Role ID로 해당 Role의 모든 Permission 조회
     *
     * @param roleId role ID
     * @return Permission Set
     */
    Set<Permission> findPermissionSetByRoleId(final Long roleId);
}
