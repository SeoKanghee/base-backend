package com.kelly.base.product.identity.auth.swagger;

import com.kelly.base.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "로그인 성공",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = CommonResponse.class),
                        examples = {
                                @ExampleObject(
                                        name = "로그아웃 성공",
                                        description = "로그아웃에 성공함",
                                        value = """
                                                {
                                                  "code": 200,
                                                  "message": "OK",
                                                  "data": null
                                                }
                                                """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "인증 실패 - 세션 정보 없음 (Spring Security)"
        )
})
public @interface PostLogoutApiResponses {
}
