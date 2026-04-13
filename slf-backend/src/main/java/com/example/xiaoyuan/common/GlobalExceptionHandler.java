package com.example.xiaoyuan.common;

import jakarta.servlet.http.HttpServletResponse; // 记得导入这个
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger; // 建议添加日志记录
import org.slf4j.LoggerFactory;

/**
 * 全局异常处理，将异常统一转为 ApiResp 响应。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ApiResp<Void> handleBiz(BusinessException e) {
        // 业务异常通常不需要打印堆栈，只记录消息即可
        log.warn("业务异常: {}", e.getMessage());
        return ApiResp.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ApiResp<Void> handleValid(Exception e) {
        String msg = "参数校验失败";
        if (e instanceof MethodArgumentNotValidException manv) {
            if (manv.getBindingResult().getFieldError() != null) {
                msg = manv.getBindingResult().getFieldError().getDefaultMessage();
            }
        } else if (e instanceof BindException be) {
            if (be.getBindingResult().getFieldError() != null) {
                msg = be.getBindingResult().getFieldError().getDefaultMessage();
            }
        }
        log.warn("参数校验失败: {}", msg);
        return ApiResp.error(ErrorCode.BAD_REQUEST, msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ApiResp<Void> handleConstraint(ConstraintViolationException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        return ApiResp.error(ErrorCode.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResp<Void> handleMalformed(HttpMessageNotReadableException e) {
        log.warn("请求体格式错误: {}", e.getMessage());
        return ApiResp.error(ErrorCode.BAD_REQUEST, "请求体格式错误");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResp<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String name = e.getName();
        Object value = e.getValue();

        String valueStr = value == null ? null : String.valueOf(value);
        if ("null".equalsIgnoreCase(valueStr) || (valueStr != null && valueStr.isBlank())) {
            log.warn("请求参数为空或非法: {}={}", name, valueStr);
            return ApiResp.error(ErrorCode.BAD_REQUEST, "参数" + name + "不能为空");
        }

        log.warn("请求参数类型不匹配: {}={}, requiredType={}", name, valueStr, e.getRequiredType());
        return ApiResp.error(ErrorCode.BAD_REQUEST, "参数" + name + "格式错误");
    }

    /**
     * 【重点修改】兜底异常处理
     * 引入 HttpServletResponse 参数，强制修改 Content-Type
     */
    @ExceptionHandler(Exception.class)
    public ApiResp<Void> handleOther(Exception e, HttpServletResponse response) {
        log.error("系统未知异常", e); // 打印完整堆栈，方便排查

        // 核心修复代码：无论请求想要什么格式（比如 .js, .css），强制返回 JSON
        response.setContentType("application/json;charset=UTF-8");

        return ApiResp.error(ErrorCode.SERVER_ERROR, "系统出现异常，请联系管理员");
    }
}
