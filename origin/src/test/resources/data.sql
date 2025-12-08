-- permission
INSERT INTO "permission" ("code", "name", "description", "created_at", "updated_at") VALUES
    ('MANAGE_MY_ACCOUNT', '내 계정정보 관리', '자신의 계정 정보를 조회/수정할 수 있는 권한', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('VIEW_ACCOUNT_LIST', '사용자 목록 조회', '사용자 목록을 조회할 수 있는 권한', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('MANAGE_ACCOUNT', '사용자 관리', '사용자를 생성/수정/삭제할 수 있는 권한', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

-- role
INSERT INTO "role" ("code", "name", "description", "created_at", "updated_at") VALUES
    ('ROLE_SITE_MANAGER', '사이트 관리자', '사이트, 시스템, 사용자 관리 권한이 있는 사용자', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('ROLE_SERVICE_ENGINEER', '서비스 엔지니어', '시스템, 사용자 관리 권한이 있는 사용자', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('ROLE_ADVANCED_USER', '고급 사용자', '고급 기능을 사용할 수 있는 사용자', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('ROLE_GENERAL_USER', '일반 사용자', '기본 기능을 사용하는 일반 사용자', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('ROLE_DEMO_USER', '데모 사용자', '데모 목적의 제한된 사용자', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

-- role_permission : ROLE_SITE_MANAGER
INSERT INTO "role_permission" ("role_id", "permission_id", "created_at")
    SELECT "r"."id", "p"."id", CURRENT_TIMESTAMP(6)
    FROM "role" "r"
    CROSS JOIN "permission" "p"
    WHERE "r"."code" = 'ROLE_SITE_MANAGER'
        AND "p"."code" IN ('MANAGE_MY_ACCOUNT', 'VIEW_ACCOUNT_LIST', 'MANAGE_ACCOUNT');

-- role_permission : ROLE_SERVICE_ENGINEER
INSERT INTO "role_permission" ("role_id", "permission_id", "created_at")
    SELECT "r"."id", "p"."id", CURRENT_TIMESTAMP(6)
    FROM "role" "r"
    CROSS JOIN "permission" "p"
    WHERE "r"."code" = 'ROLE_SERVICE_ENGINEER'
        AND "p"."code" IN ('MANAGE_MY_ACCOUNT', 'VIEW_ACCOUNT_LIST', 'MANAGE_ACCOUNT');

-- role_permission : ROLE_ADVANCED_USER
INSERT INTO "role_permission" ("role_id", "permission_id", "created_at")
    SELECT "r"."id", "p"."id", CURRENT_TIMESTAMP(6)
    FROM "role" "r"
    CROSS JOIN "permission" "p"
    WHERE "r"."code" = 'ROLE_ADVANCED_USER'
        AND "p"."code" IN ('MANAGE_MY_ACCOUNT');

-- role_permission : ROLE_GENERAL_USER
INSERT INTO "role_permission" ("role_id", "permission_id", "created_at")
    SELECT "r"."id", "p"."id", CURRENT_TIMESTAMP(6)
    FROM "role" "r"
    CROSS JOIN "permission" "p"
    WHERE "r"."code" = 'ROLE_GENERAL_USER'
        AND "p"."code" IN ('MANAGE_MY_ACCOUNT');

-- role_permission : ROLE_DEMO_USER
INSERT INTO "role_permission" ("role_id", "permission_id", "created_at")
    SELECT "r"."id", "p"."id", CURRENT_TIMESTAMP(6)
    FROM "role" "r"
    CROSS JOIN "permission" "p"
    WHERE "r"."code" = 'ROLE_DEMO_USER'
        AND "p"."code" IN ('MANAGE_MY_ACCOUNT');

-- password hash generator : https://bcrypt-generator.com/
INSERT INTO "account" ("login_id", "password", "name", "role", "status", "fail_count", "is_temp", "language_code", "department", "password_expired_at", "is_hidden", "created_at", "updated_at") VALUES
    ('master', 'need_to_encrypt', '시스템 관리자', 'ROLE_SITE_MANAGER', 'ACTIVE', '0', '0', 'en', '마스터 계정', '2125-11-11 00:00:00', '1', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('admin', 'need_to_encrypt', '서비스 엔지니어', 'ROLE_SERVICE_ENGINEER', 'ACTIVE', '0', '0', 'en', 'SE', '2125-11-11 00:00:00', '1', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('adv_user', '$2a$12$bzSRWeLtB3O8L6rA./N7euDW2AMpOnFhro/rlU5evVw2PDZATKlvO', '전문 사용자', 'ROLE_ADVANCED_USER', 'ACTIVE', '0', '1', 'en', '', '2125-11-11 00:00:00', '0', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('gen-user', '$2a$12$obPNe.1aVEDOpgWvPgafh.Hakv74bxJ8h8XzSxgIRDahG4m/vok1y', '일반 사용자', 'ROLE_GENERAL_USER', 'ACTIVE', '0', '0', 'en', '', '2125-11-11 00:00:00', '0', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('failure5', '$2a$12$AaWRCQkgOEY0F2HiSfKKTeWuAfurKWhzSO3idaS.uuRQGxirCorNm', '비번 5번 틀림', 'ROLE_GENERAL_USER', 'ACTIVE', '5', '0', 'en', '', '2125-11-11 00:00:00', '0', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('disable', '$2a$12$ESKLuREE0ukb4l1GI3cbgev8kYQz0M/MCpwa92xwBAEGDPRw5YIqK', '비활성화 계정', 'ROLE_GENERAL_USER', 'INACTIVE', '0', '0', 'en', '', '2125-11-11 00:00:00', '0', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('invalid', '$2a$12$PzUBbrOzBixeJY1MPChCK.5tCMLSgG3Z5gkBg14s/oETQskVbjMcq', '정보가 잘못된 계정', 'ROLE_GENERAL_USER', 'LOCKED', '0', '0', 'en', '', '2125-11-11 00:00:00', '0', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('unknown_role', '$2a$12$4tuIpSCBQKP3MRi9gCj3Nu.mLh4h3r2OGd7ptihbbUFS9y.WvbXi6', 'Role 이 잘못된 계정', 'ROLE_WRONG', 'ACTIVE', '0', '0', 'en', '', '2125-11-11 00:00:00', '0', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
