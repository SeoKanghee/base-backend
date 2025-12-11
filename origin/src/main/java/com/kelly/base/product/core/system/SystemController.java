package com.kelly.base.product.core.system;

import com.kelly.base.common.i18n.I18nMessageService;
import com.kelly.base.common.response.CommonResponse;
import com.kelly.base.common.response.CommonResultCode;
import com.kelly.base.product.shared.permission.annotation.RequirePermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.kelly.base.product.shared.Constants.UrlInfo.URI_ROOT_SYSTEM;

/**
 * system controller
 *
 * @author 서강희
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "System", description = "API for system")
@RestController
@RequestMapping(value = URI_ROOT_SYSTEM)
public class SystemController {
    private final I18nMessageService i18nMessageService;    // common package service

    /**
     * 메시지 리소스 동적 리로드
     * 외부 리소스 파일 변경 후 애플리케이션 재시작 없이 메시지 갱신
     *
     * @return 성공/실패 응답
     */
    @GetMapping("/i18n/reload")
    @RequirePermission("MANAGE_SYSTEM")
    @Operation(summary = "다국어 메시지 리로드", description = "다국어 메시지 리소스를 리로드합니다")
    public CommonResponse<String> reloadMessages() {
        log.info("reloading i18n messages via API");
        i18nMessageService.reload();
        return new CommonResponse<>(CommonResultCode.SUCCESS);
    }
}
