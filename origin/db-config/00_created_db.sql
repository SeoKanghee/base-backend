SET NAMES utf8;
SET time_zone = '+00:00';

-- [create database]
CREATE DATABASE IF NOT EXISTS `base_backend`;
USE `base_backend`;

-- [drop tables]
DROP TABLE IF EXISTS `base_backend`.`role_permission`;
DROP TABLE IF EXISTS `base_backend`.`role`;
DROP TABLE IF EXISTS `base_backend`.`permission`;
DROP TABLE IF EXISTS `base_backend`.`account`;

-- [create tables]
CREATE TABLE `base_backend`.`account` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Sequence ID',
    login_id VARCHAR(20) NOT NULL UNIQUE COMMENT 'ID',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호(암호화)',
    name VARCHAR(64) NOT NULL COMMENT '이름',
    role VARCHAR(100) NOT NULL DEFAULT 'ROLE_GENERAL_USER' COMMENT '사용자 역할 - role.code',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '사용자 상태',
    fail_count BIGINT NOT NULL DEFAULT 0 COMMENT '로그인 시도 실패 횟수',
    is_temp BOOLEAN NOT NULL DEFAULT TRUE COMMENT '임시 비밀번호 사용 여부',
    department VARCHAR(128) COMMENT '부서',
    memo VARCHAR(512) COMMENT '메모',
    last_login_at DATETIME(6) NULL COMMENT '마지막으로 로그인한 시간',
    lockout_expired_at DATETIME(6) NULL COMMENT '잠금 만료 시간',
    password_expired_at DATETIME(6) NOT NULL COMMENT '비밀번호 만료 시간',
    is_hidden BOOLEAN NOT NULL DEFAULT FALSE COMMENT '목록 노출 제외',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',

    INDEX idx_account_login_id (login_id),
    INDEX idx_account_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 정보';

CREATE TABLE `base_backend`.`permission` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Sequence ID',
    code VARCHAR(100) UNIQUE NOT NULL COMMENT '권한 코드',
    name VARCHAR(255) NOT NULL COMMENT '권한 이름',
    description VARCHAR(500) COMMENT '권한 설명',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',

    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='권한 테이블';

CREATE TABLE `base_backend`.`role` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Sequence ID',
    code VARCHAR(100) UNIQUE NOT NULL COMMENT '역할 코드',
    name VARCHAR(255) NOT NULL COMMENT '역할 이름',
    description VARCHAR(500) COMMENT '역할 설명',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '수정일시',

    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='역할 테이블';

CREATE TABLE `base_backend`.`role_permission` (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Sequence ID',
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '생성일시',

    FOREIGN KEY fk_role (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY fk_permission (permission_id) REFERENCES permission(id) ON DELETE CASCADE,
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='역할-권한 매핑 테이블';

-- [init data]
INSERT INTO `base_backend`.`permission` (code, name, description) VALUES
    ('MANAGE_MY_ACCOUNT', '내 계정정보 관리', '자신의 계정 정보를 조회/수정할 수 있는 권한'),
    ('VIEW_ACCOUNT_LIST', '사용자 목록 조회', '사용자 목록을 조회할 수 있는 권한'),
    ('MANAGE_ACCOUNT', '사용자 관리', '사용자를 생성/수정/삭제할 수 있는 권한');

INSERT INTO `base_backend`.`role` (code, name, description) VALUES
    ('ROLE_SITE_MANAGER', '사이트 관리자', '사이트, 시스템, 사용자 관리 권한이 있는 사용자'),
    ('ROLE_SERVICE_ENGINEER', '서비스 엔지니어', '시스템, 사용자 관리 권한이 있는 사용자'),
    ('ROLE_ADVANCED_USER', '고급 사용자', '고급 기능을 사용할 수 있는 사용자'),
    ('ROLE_GENERAL_USER', '일반 사용자', '기본 기능을 사용하는 일반 사용자'),
    ('ROLE_DEMO_USER', '데모 사용자', '데모 목적의 제한된 사용자');

-- role_permission : ROLE_SITE_MANAGER
INSERT INTO `base_backend`.`role_permission` (role_id, permission_id)
    SELECT r.id, p.id
    FROM role r
    CROSS JOIN permission p
    WHERE r.code = 'ROLE_SITE_MANAGER'
        AND p.code IN ('MANAGE_MY_ACCOUNT', 'VIEW_ACCOUNT_LIST', 'MANAGE_ACCOUNT');

-- role_permission : ROLE_SERVICE_ENGINEER
INSERT INTO `base_backend`.`role_permission` (role_id, permission_id)
    SELECT r.id, p.id
    FROM role r
    CROSS JOIN permission p
    WHERE r.code = 'ROLE_SERVICE_ENGINEER'
        AND p.code IN ('MANAGE_MY_ACCOUNT', 'VIEW_ACCOUNT_LIST', 'MANAGE_ACCOUNT');

-- role_permission : ROLE_ADVANCED_USER
INSERT INTO `base_backend`.`role_permission` (role_id, permission_id)
    SELECT r.id, p.id
    FROM role r
    CROSS JOIN permission p
    WHERE r.code = 'ROLE_ADVANCED_USER'
        AND p.code IN ('MANAGE_MY_ACCOUNT');

-- role_permission : ROLE_GENERAL_USER
INSERT INTO `base_backend`.`role_permission` (role_id, permission_id)
    SELECT r.id, p.id
    FROM role r
    CROSS JOIN permission p
    WHERE r.code = 'ROLE_GENERAL_USER'
        AND p.code IN ('MANAGE_MY_ACCOUNT');

-- role_permission : ROLE_DEMO_USER
INSERT INTO `base_backend`.`role_permission` (role_id, permission_id)
    SELECT r.id, p.id
    FROM role r
    CROSS JOIN permission p
    WHERE r.code = 'ROLE_DEMO_USER'
        AND p.code IN ('MANAGE_MY_ACCOUNT');

INSERT INTO `base_backend`.`account` (login_id, password, name, role, status, fail_count, is_temp, department, password_expired_at, is_hidden) VALUES
    ('master', '$2a$12$RCtTO21kUB3pmMy5oLXhn.kQrkxwvg8/xM2XPMf3dp3aJ91A8eNuW', '시스템 관리자', 'ROLE_SITE_MANAGER', 'ACTIVE', 0, 0, '마스터 계정', TIMESTAMP('2125-11-11 00:00:00'), 1),
    ('admin', '$2a$12$lqH4bS1GbawmstuChZD2WeNqmPSRgkkM55TSIHcnMWUkDxHC2dfd2', '서비스 엔지니어', 'ROLE_SERVICE_ENGINEER', 'ACTIVE', 0, 0, 'SE', TIMESTAMP('2125-11-11 00:00:00'), 1);
