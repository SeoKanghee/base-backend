package com.kelly.base.common.exception;

import com.kelly.base.common.dto.CommonResponse;
import com.kelly.base.common.enums.CommonResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;

import static com.kelly.base.common.Constants.OrderInfo.EXCEPTION_HANDLER_ORDER_COMMON;

@Slf4j
@RestControllerAdvice
@Order(EXCEPTION_HANDLER_ORDER_COMMON)
public class CommonExceptionHandler {
    @ExceptionHandler(CommonException.class)
    public ResponseEntity<CommonResponse<CommonResultCode>> handleCommonException(
            HttpServletRequest req, final CommonException e
    ) {
        log.error("URI : {}, CommonException info : {}", req.getRequestURI(), e.toString());

        // extra code 및 message 가 존재할 경우 교체
        final Integer code = e.getExtraCode()!=null ? e.getExtraCode():e.getResultCode().getCode();
        final String message = e.getExtraMessage()!=null ? e.getExtraMessage():e.getResultCode().getMessage();
        final CommonResponse<CommonResultCode> response = new CommonResponse<>(code, message);

        return new ResponseEntity<>(response, e.getResultCode().getHttpStatus());
    }

    @ExceptionHandler(CommonRuntimeException.class)
    public ResponseEntity<CommonResponse<CommonResultCode>> handleCommonRuntimeException(
            HttpServletRequest req, final CommonRuntimeException e
    ) {
        log.error("URI : {}, CommonRuntimeException info : {}", req.getRequestURI(), e.toString());

        // extra code 및 message 가 존재할 경우 교체
        final Integer code = e.getExtraCode()!=null ? e.getExtraCode():e.getResultCode().getCode();
        final String message = e.getExtraMessage()!=null ? e.getExtraMessage():e.getResultCode().getMessage();
        final CommonResponse<CommonResultCode> response = new CommonResponse<>(code, message);

        return new ResponseEntity<>(response, e.getResultCode().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)    // @Valid 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleMethodArgumentNotValidException(
            HttpServletRequest req, MethodArgumentNotValidException e
    ) {
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        log.error("MethodArgumentNotValidException - URI : {}, Error Message : {}", req.getRequestURI(), errors);
        return new CommonResponse<>(CommonResultCode.INVALID_PARAMETER);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)   // @Valid 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleHandlerMethodValidationException(
            HttpServletRequest req, HandlerMethodValidationException e
    ) {
        List<String> errors = e.getAllErrors().stream().map(MessageSourceResolvable::getDefaultMessage).toList();
        log.error("HandlerMethodValidationException - URI : {}, Error Message : {}", req.getRequestURI(), errors);
        return new CommonResponse<>(CommonResultCode.INVALID_PARAMETER);
    }

    @ExceptionHandler(ConstraintViolationException.class)   // @NotNull 처리
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CommonResponse<Void> handleConstraintViolationException(
            HttpServletRequest req, ConstraintViolationException e
    ) {
        log.error("URI : {}, Error Message : {}, Violations : {}",
                req.getRequestURI(), e.getMessage(), e.getConstraintViolations());
        return new CommonResponse<>(CommonResultCode.INVALID_PARAMETER);
    }

    @ExceptionHandler(TaskRejectedException.class)  // @Async 처리
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public CommonResponse<Void> handleTaskRejectedException(
            HttpServletRequest req, TaskRejectedException e
    ) {
        log.error("TaskRejectedException - URI : {}, Error Message : {}", req.getRequestURI(), e.getMessage());
        return new CommonResponse<>(CommonResultCode.TASK_REJECTED);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<Void> handleException(HttpServletRequest req, Throwable e) {
        log.error("URI : {}, Exception Class : {}, Message : {}", req.getRequestURI(), e.getClass(), e.getMessage());
        return new CommonResponse<>(CommonResultCode.INTERNAL_SERVER_ERROR);
    }
}
