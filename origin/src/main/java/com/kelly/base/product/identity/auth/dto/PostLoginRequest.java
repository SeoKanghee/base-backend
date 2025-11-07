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
        String password
) {
    // loginId : 계정 ID
    // password : 계정 비밀번호
}
