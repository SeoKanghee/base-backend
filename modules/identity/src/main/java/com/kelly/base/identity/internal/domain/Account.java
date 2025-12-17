package com.kelly.base.identity.internal.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.ZonedDateTime;

import static com.kelly.base.common.CommonConstants.RoleCode.ROLE_GENERAL_USER;

/**
 * account entity
 *
 * @author 서강희
 */
@Entity
@Getter
@ToString
@SuperBuilder
@EntityListeners(value = AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Builder.Default
    @Column(name = "role", nullable = false, length = 100)
    private String role = ROLE_GENERAL_USER;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Builder.Default
    @Column(name ="fail_count", nullable = false)
    private Long failCount = 0L;

    @Builder.Default
    @Column(name ="is_temp", nullable = false)
    private Boolean isTemp = true;

    @Builder.Default
    @Column(name = "language_code", nullable = false)
    private String languageCode = "en";

    @Column(name = "department", length = 128)
    private String department;

    @Column(name = "memo")
    private String memo;

    @Column(name = "last_login_at")
    private ZonedDateTime lastLoginAt;

    @Column(name = "lockout_expired_at")
    private ZonedDateTime lockoutExpiredAt;

    @Column(name = "password_expired_at", nullable = false)
    private ZonedDateTime passwordExpiredAt;

    @Builder.Default
    @Column(name ="is_hidden", nullable = false)
    private Boolean isHidden = false;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    public void updateProfile(final String name, final String department, final String memo) {
        this.name = name;
        this.department = department;
        this.memo = memo;
    }

    public void lockAccount(final ZonedDateTime lockoutExpiredAt) {
        this.failCount = 0L;
        this.status = AccountStatus.LOCKED;
        this.lockoutExpiredAt = lockoutExpiredAt;
    }

    public void unlockAccount() {
        this.failCount = 0L;
        this.status = AccountStatus.ACTIVE;
        this.lockoutExpiredAt = null;
    }

    public void changePassword(final String newPassword, final ZonedDateTime passwordExpiredAt) {
        this.password = newPassword;
        this.passwordExpiredAt = passwordExpiredAt;
    }

    public void updateStatus(final AccountStatus status) {
        this.status = status;
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public boolean isFirstLogin() {
        return Boolean.TRUE.equals(this.isTemp);
    }

    public void increaseFailCount() {
        this.failCount += 1L;
    }

    public void initFailCount() {
        this.failCount = 0L;
    }

    public void recordLoginTimestamp(final ZonedDateTime loginAt) {
        this.lastLoginAt = loginAt;
    }
}
