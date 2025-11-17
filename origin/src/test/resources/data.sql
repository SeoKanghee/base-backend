-- password hash generator : https://bcrypt-generator.com/
INSERT INTO "account" ("login_id", "password", "name", "role", "status", "fail_count", "is_temp", "department", "password_expired_at", "created_at", "updated_at") VALUES
    ('master', 'need_to_encrypt', '시스템 관리자', 'SITE_MANAGER', 'ACTIVE', '0', '0', '마스터 계정', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('admin', 'need_to_encrypt', '서비스 엔지니어', 'SERVICE_ENGINEER', 'ACTIVE', '0', '0', 'SE', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('adv_user', '$2a$12$bzSRWeLtB3O8L6rA./N7euDW2AMpOnFhro/rlU5evVw2PDZATKlvO', '전문 사용자', 'ADVANCED_USER', 'ACTIVE', '0', '1', '', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('gen-user', '$2a$12$obPNe.1aVEDOpgWvPgafh.Hakv74bxJ8h8XzSxgIRDahG4m/vok1y', '일반 사용자', 'GENERAL_USER', 'ACTIVE', '0', '0', '', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('failure5', '$2a$12$AaWRCQkgOEY0F2HiSfKKTeWuAfurKWhzSO3idaS.uuRQGxirCorNm', '비번 5번 틀림', 'GENERAL_USER', 'ACTIVE', '5', '0', '', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('disable', '$2a$12$ESKLuREE0ukb4l1GI3cbgev8kYQz0M/MCpwa92xwBAEGDPRw5YIqK', '비활성화 계정', 'GENERAL_USER', 'INACTIVE', '0', '0', '', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('invalid', '$2a$12$PzUBbrOzBixeJY1MPChCK.5tCMLSgG3Z5gkBg14s/oETQskVbjMcq', '정보가 잘못된 계정', 'GENERAL_USER', 'LOCKED', '0', '0', '', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
