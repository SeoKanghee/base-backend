/**
 * <b>Common Module</b>
 * <p>
 * 이 모듈은 애플리케이션 전체에서 공통으로 사용되는 기능을 제공합니다.<br>
 * 모든 다른 모듈에서 의존할 수 있는 공유 모듈입니다.<br>
 * 외부에서 참조가 필요한 내용은 <code>package-info.java</code> 를 통해 공유합니다.<br>
 *
 * <p>
 * 제공되는 전체 모듈:
 * <ul>
 *   <li>common - 상수 정의</li>
 *   <li>common::exceptions - 예외 정의</li>
 *   <li>common::responses - 응답 정의</li>
 *   <li>common::interfaces - interface 정의</li>
 *   <li>common::utils - 유틸 클래스 정의</li>
 * </ul>
 *
 * @author 서강희
 */
@org.springframework.modulith.ApplicationModule(
        displayName = "Common Module"
)
package com.kelly.base.common;
