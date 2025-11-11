SET NAMES utf8;

-- create database
CREATE DATABASE IF NOT EXISTS `base_backend`;
USE `base_backend`;

-- drop tables
DROP TABLE IF EXISTS `base_backend`.`account`;

-- create tables
CREATE TABLE `base_backend`.`account` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Sequence ID',
    login_id VARCHAR(20) NOT NULL UNIQUE COMMENT 'ID',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호(암호화)',
    name VARCHAR(64) NOT NULL COMMENT '이름',
    role VARCHAR(32) NOT NULL DEFAULT 'GENERAL_USER' COMMENT '사용자 역할',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '사용자 상태',
    fail_count BIGINT NOT NULL DEFAULT 0 COMMENT '로그인 시도 실패 횟수',
    is_temp BOOLEAN NOT NULL DEFAULT TRUE COMMENT '임시 비밀번호 사용 여부',
    department VARCHAR(128) COMMENT '부서',
    memo VARCHAR(512) COMMENT '메모',
    lockout_expired_at DATETIME(6) NULL COMMENT '잠금 만료 시간',
    password_expired_at DATETIME(6) NOT NULL COMMENT '비밀번호 만료 시간',
    created_at DATETIME(6) NOT NULL COMMENT '생성일시',
    updated_at DATETIME(6) NOT NULL COMMENT '수정일시',

    INDEX idx_account_login_id (login_id),
    INDEX idx_account_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 정보';

-- init data
INSERT INTO `base_backend`.`account` (login_id, password, name, role, status, fail_count, is_temp, department, password_expired_at, created_at, updated_at) VALUES
    ('master', 'FfihV0TUqJzX+Is+WvMY5CckI1UlHP3Xg7JTuNDCOT9wvUurU9Wm6Ko=', '시스템 관리자', 'SITE_MANAGER', 'ACTIVE', 0, 0, '마스터 계정', TIMESTAMP('2125-11-11 00:00:00'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('admin', 'QRgAlRgDFkGhpci1h4CtkC/j3h/eqktdxg+SfpiZqcrj+Fv4DIFb9g==', '서비스 엔지니어', 'SERVICE_ENGINEER', 'ACTIVE', 0, 0, 'SE', TIMESTAMP('2125-11-11 00:00:00'), CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
