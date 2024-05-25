package api.development.platform.exception;

import api.development.platform.common.BaseResponse;
import api.development.platform.common.ErrorCode;
import api.development.platform.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice // 可以处理全局的异常、数据绑定、验证等
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class) // 异常处理器，处理BusinessException 类型的异常
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e); // 日志输出
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class) // 编译错误
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
