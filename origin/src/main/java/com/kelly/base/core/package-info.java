/**
 * <b>Core Module</b><p>
 * 이 모듈은 시스템 핵심 기능을 담당합니다.
 * <p>
 * 주요 기능:
 * <ul>
 *   <li>시스템 설정 (system)</li>
 * </ul>
 * <p>
 * 의존 모듈:
 * <ul>
 *   <li>common</li>
 *   <li>common::exceptions</li>
 *   <li>common::responses</li>
 *   <li>common::interfaces</li>
 *   <li>common::utils</li>
 *   <li>identity::perm-annotation</li>
 * </ul>
 *
 * @author 서강희
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Core Module",
        allowedDependencies = {
                "common", "common::exceptions", "common::responses", "common::interfaces", "common::utils",
                "identity::perm-annotation"
        }
)
package com.kelly.base.core;