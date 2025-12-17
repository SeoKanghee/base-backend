package com.kelly.base.product.identity.auth;

import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.product.identity.auth.dto.PostLoginRequest;
import com.kelly.base.product.identity.auth.swagger.PostLoginApiResponses;
import com.kelly.base.product.identity.auth.swagger.PostLogoutApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kelly.base.common.CommonConstants.UrlInfo.URI_ROOT_AUTH;

/**
 * authentication controller
 *
 * @author 서강희
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for authentication")
@RestController
@RequestMapping(value = URI_ROOT_AUTH)
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "로그인 요청")
    @PostLoginApiResponses
    @PostMapping(value = "/login")
    public CommonResponse<Void> postLogin(
            @RequestBody @Valid final PostLoginRequest requestPayload, HttpServletRequest servletRequest
    ) {
        log.info("[post] login");
        return authService.login(requestPayload, servletRequest);
    }

    @Operation(summary = "로그아웃 요청")
    @PostLogoutApiResponses
    @PostMapping(value = "/logout")
    public CommonResponse<Void> postLogout(HttpServletRequest servletRequest) {
        log.info("[post] logout");
        return authService.logout(servletRequest);
    }
}
