INSERT INTO "users" ("login_id", "password", "name", "role", "status", "fail_count", "is_temp", "department", "password_expired_at", "created_at", "updated_at") VALUES
    ('master', 'need_to_encrypt', '시스템 관리자', 'SITE_MANAGER', 'ACTIVE', '0', '0', '마스터 계정', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6)),
    ('admin', 'need_to_encrypt', '서비스 엔지니어', 'SERVICE_ENGINEER', 'ACTIVE', '0', '0', 'SE', '2125-11-11 00:00:00', CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));
