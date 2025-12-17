/**
 * <b>Identity Module</b><p>
 * 이 모듈은 사용자 인증, 계정 관리, 권한 관리를 담당합니다.
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>사용자 인증 (auth)</li>
 *   <li>계정 관리 (accounts)</li>
 *   <li>권한 관리 (permission, role)</li>
 * </ul>
 * <p>
 * 의존 모듈:
 * <ul>
 *   <li>common</li>
 *   <li>common::exceptions</li>
 *   <li>common::responses</li>
 *   <li>common::interfaces</li>
 *   <li>common::utils</li>
 * </ul>
 *
 * @author kelly.seo
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Identity Module",
        allowedDependencies = {
                "common", "common::exceptions", "common::responses", "common::interfaces", "common::utils"
        }
)
package com.kelly.base.product.identity;