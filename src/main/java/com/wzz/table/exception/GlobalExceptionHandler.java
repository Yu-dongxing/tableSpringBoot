package com.wzz.table.exception;

import cn.dev33.satoken.exception.SaTokenException;
import com.wzz.table.DTO.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("不支持的请求方法: {}, 错误信息={}", e.getMessage(), e);
        return Result.error("不支持的请求方法");
    }

    // 参数校验异常-方法参数校验
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() : "参数校验失败";
        log.warn("参数校验异常: {}, 错误信息={}", msg, e);
        return Result.error(400, msg);
    }

    // 参数校验异常-BindException
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldError() != null ?
                e.getBindingResult().getFieldError().getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定异常: {}, 错误信息={}", msg, e);
        return Result.error(400, msg);
    }

    // JSON解析失败
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求体JSON解析失败: {}, 错误信息={}", e.getMessage(), e);
        return Result.error(400, "请求参数格式错误");
    }

    // SaToken异常
    @ExceptionHandler(SaTokenException.class)
    public Result<?> handleSaTokenException(SaTokenException e) {
        int code = e.getCode();
        String msg;
        switch (code) {
            case 11001: msg = "未能读取到有效Token"; code = 101; break;
            case 11002: msg = "登录时的账号id值为空"; code = 102; break;
            case 11003: msg = "更改Token指向的账号Id时，账号Id值为空"; code = 103; break;
            case 11011: msg = "未能读取到有效Token"; code = 104; break;
            case 11012: msg = "Token无效"; code = 105; break;
            case 11013: msg = "Token已过期"; code = 106; break;
            case 11014: msg = "Token已被顶下线"; code = 107; break;
            case 11015: msg = "Token已被踢下线"; code = 108; break;
            case 11016: msg = "Token已被冻结"; code = 109; break;
            case 11041: msg = "无权限，请联系管理员"; code = 403; break;
            case 11042: msg = "无此角色权限：" + e.getMessage(); code = 404; break;
            default:
                log.error("未知SaToken异常: code={}, msg={}， 错误信息={}", code, e.getMessage(), e);
                return Result.error(500, "认证服务器错误");
        }
        log.warn("SaToken鉴权异常: code={}, msg={}, 错误信息={}", code, msg, e);
        return Result.error(code, msg);
    }

    // 可以添加自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage(), e);
        return Result.error(e.getCode(), e.getMessage());
    }

    // 全局异常兜底
    @ExceptionHandler(Exception.class)
    public Result<?> handleAllException(Exception e) {
        log.error("服务器未知异常:", e);
        return Result.error(500, "系统繁忙，请稍后再试");
    }
}
