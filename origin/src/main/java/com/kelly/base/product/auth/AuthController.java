package com.kelly.base.product.auth;

import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.product.auth.dto.PostLoginRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kelly.base.product.Constants.UrlInfo.URI_ROOT_AUTH;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for authentication")
@RestController
@RequestMapping(value = URI_ROOT_AUTH)
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인 요청")
    @PostMapping(value = "/login")
    public CommonResponse<Void> postLogin(
            @RequestBody @Valid final PostLoginRequest requestPayload
    ) {
        log.info("[post] login");
        return authService.login(requestPayload);
    }
}
