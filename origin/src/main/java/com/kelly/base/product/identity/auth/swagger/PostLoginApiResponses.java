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
                                        name = "로그인 성공",
                                        description = "로그인에 성공함",
                                        value = """
                                                {
                                                  "code": 200,
                                                  "message": "OK",
                                                  "data": null
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "비밀번호 변경 필요",
                                        description = "로그인에 성공했지만 비밀번호 변경이 필요함",
                                        value = """
                                                {
                                                  "code": 82000001,
                                                  "message": "[OK] password change is required",
                                                  "data": null
                                                }
                                                """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "401",
                description = "인증 실패",
                content = @Content(
                        mediaType = "application/json",
                        examples = {
                                @ExampleObject(
                                        name = "잘못된 인증 정보",
                                        description = "로그인 ID 또는 비밀번호가 일치하지 않음",
                                        value = """
                                                {
                                                  "code": 84010001,
                                                  "message": "[UNAUTHORIZED] invalid userId or password",
                                                  "data": null
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "이미 로그인됨",
                                        description = "다른 세션이 활성화되어 있어 로그인이 불가함 (force 값이 true 가 아님)",
                                        value = """
                                                {
                                                  "code": 84010002,
                                                  "message": "[UNAUTHORIZED] already logged in",
                                                  "data": null
                                                }
                                                """
                                )
                        }
                )
        ),
        @ApiResponse(
                responseCode = "403",
                description = "계정 접근 불가",
                content = @Content(
                        mediaType = "application/json",
                        examples = {
                                @ExampleObject(
                                        name = "계정 잠김",
                                        description = "로그인 실패 6회 이상으로 계정이 잠김 (30분)",
                                        value = """
                                                {
                                                  "code": 84030001,
                                                  "message": "[FORBIDDEN] this account has been locked",
                                                  "data": null
                                                }
                                                """
                                ),
                                @ExampleObject(
                                        name = "계정 비활성화",
                                        description = "계정이 비활성화 상태임",
                                        value = """
                                                {
                                                  "code": 84030002,
                                                  "message": "[FORBIDDEN] this account is currently disabled",
                                                  "data": null
                                                }
                                                """
                                )
                        }
                )
        )
})
public @interface PostLoginApiResponses {
}