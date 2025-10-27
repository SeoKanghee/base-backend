package com.kelly.base.product.auth;

import com.kelly.base.common.dto.CommonResponse;
import com.kelly.base.common.enums.CommonResultCode;
import com.kelly.base.product.auth.dto.PostLoginRequest;
import com.kelly.base.product.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;

    public CommonResponse<Void> login(@NonNull PostLoginRequest requestPayload) {
        log.debug("login info : {}", requestPayload);

        // TODO: need to impl
        // 1. DB 에서 계정 정보를 읽어옴 ( userRepository )
        // 2. 계정이 잠겨 있음 -> response 403 Forbidden
        // 3. 비밀번호가 틀림 -> response 401 Unauthorized
        // 4. 비밀번호가 맞음 ( 최초 로그인 아님 ) -> response 200 OK
        // 5. 비밀번호가 맞음 ( 최초 로그인 ) -> response 200 OK ( NEED_CHANGE_PASSWORD )

        return new CommonResponse<>(CommonResultCode.SUCCESS);
    }
}
