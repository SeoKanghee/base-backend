INSERT INTO "users" ("login_id", "password", "name", "role", "status", "department", "created_at", "updated_at") VALUES
    ('master', 'need_to_encrypt', '시스템 관리자', 'SITE_MANAGER', 'ACTIVE', '마스터 계정', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('admin', 'need_to_encrypt', '서비스 엔지니어', 'SERVICE_ENGINEER', 'ACTIVE', 'SE', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
