package com.kelly.base.product.identity.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "payload - post login")
public record PostLoginRequest(
        @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "test_user")
        @NotBlank(message = "loginId is empty")
        String loginId,

        @Schema(description = "비밀번호", requiredMode = Schema.RequiredMode.REQUIRED, example = "**********")
        @NotBlank(message = "password is empty")
        String password,

        @Schema(description = "강제 로그인 여부 (true: 기존 세션 무효화, false: 기존 세션 유지)", example = "false")
        Boolean force
) {
    // loginId : 계정 ID
    // password : 계정 비밀번호
    // force : 기존 세션이 존재할 때 강제로 로그인할지 여부 (기본값: false)
}
