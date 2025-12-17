package com.kelly.base.identity.internal.domain.repository.custom;

import com.kelly.base.identity.internal.domain.Permission;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kelly.base.identity.internal.domain.QPermission.permission;
import static com.kelly.base.identity.internal.domain.QRolePermission.rolePermission;

@RequiredArgsConstructor
public class PermissionRepositoryCustomImpl implements PermissionRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Set<Permission> findPermissionSetByRoleId(Long roleId) {
        // JPQL 참고 :
        // SELECT p FROM Permission p
        // JOIN RolePermission rp ON rp.permission.id = p.id
        // WHERE rp.role.id = :roleId
        final List<Permission> interimResult
                = queryFactory.select(permission).from(rolePermission)
                              .join(rolePermission.permission, permission)
                              .where(rolePermission.role.id.eq(roleId))
                              .fetch();
        return new HashSet<>(interimResult);
    }
}
