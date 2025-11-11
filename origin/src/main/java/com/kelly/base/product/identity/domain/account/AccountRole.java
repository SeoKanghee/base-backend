package com.kelly.base.product.identity.domain.account;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AccountRole {
    SITE_MANAGER("ROLE_SITE_MANAGER"),
    ADVANCED_USER("ROLE_ADVANCED_USER"),
    GENERAL_USER("ROLE_GENERAL_USER"),
    DEMO_USER("ROLE_DEMO_USER"),
    SERVICE_ENGINEER("ROLE_SERVICE_ENGINEER");

    private final String authority;
}
