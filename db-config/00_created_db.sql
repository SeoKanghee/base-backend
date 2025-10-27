SET NAMES utf8;

-- create database
CREATE DATABASE IF NOT EXISTS `base_backend`;
USE `base_backend`;

-- drop tables
DROP TABLE IF EXISTS `base_backend`.`users`;

-- create tables
CREATE TABLE `base_backend`.`users` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Sequence ID',
    login_id VARCHAR(20) NOT NULL UNIQUE COMMENT 'ID',
    password VARCHAR(255) NOT NULL COMMENT '비밀번호(암호화)',
    name VARCHAR(64) NOT NULL COMMENT '이름',
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT '사용자 상태',
    role VARCHAR(32) NOT NULL DEFAULT 'GENERAL_USER' COMMENT '사용자 역할',
    department VARCHAR(128) COMMENT '부서',
    memo VARCHAR(512) COMMENT '메모',
    created_at TIMESTAMP(6) NOT NULL COMMENT '생성일시',
    updated_at TIMESTAMP(6) NOT NULL COMMENT '수정일시',

    INDEX idx_users_login_id (login_id),
    INDEX idx_users_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 정보';

-- init data
INSERT INTO `base_backend`.`users` (login_id, password, name, role, status, department, created_at, updated_at) VALUES
    ('master', 'need_to_encrypt', '시스템 관리자', 'SITE_MANAGER', 'ACTIVE', '마스터 계정', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('admin', 'need_to_encrypt', '서비스 엔지니어', 'SERVICE_ENGINEER', 'ACTIVE', 'SE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
