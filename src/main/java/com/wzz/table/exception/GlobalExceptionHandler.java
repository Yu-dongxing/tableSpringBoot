package com.wzz.table.exception;

import cn.dev33.satoken.exception.SaTokenException;
import com.wzz.table.DTO.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result handleCorsException(HttpRequestMethodNotSupportedException e) {
        System.out.println("请求错误！："+e.getMessage());
        return Result.error("不支持的请求方法");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        System.out.println("请求错误2："+e.getMessage());
        return Result.error("操作失败");
    }
    @ExceptionHandler(SaTokenException.class)
    public Result handlerSaTokenException(SaTokenException e) {
        // 根据不同异常细分状态码返回不同的提示 11002-3 11011-11016
        if(e.getCode() == 11001) {
            return Result.error(101,"未能读取到有效Token");
        }
        if(e.getCode() == 11002) {
            return Result.error(102,"登录时的账号id值为空");
        }
        if(e.getCode() == 11003) {
            return Result.error(103,"更改 Token 指向的 账号Id 时，账号Id值为空");
        }
        if(e.getCode() == 11011) {
            return Result.error(104,"未能读取到有效Token");
        }
        if(e.getCode() == 11012) {
            return Result.error(105,"Token无效");
        }
        if(e.getCode() == 11013) {
            return Result.error(106,"Token已过期");
        }
        if(e.getCode() == 11014) {
            return Result.error(107,"Token已被顶下线");
        }
        if(e.getCode() == 11015) {
            return Result.error(108,"Token已被踢下线");
        }
        if(e.getCode() == 11016) {
            return Result.error(109,"Token已被冻结");
        }
        if(e.getCode() == 11041) {
            return Result.error(403, "无权限，请联系管理员");
        }
        if(e.getCode() == 11042) {
            return Result.error(404, "无此角色权限：" + e.getMessage());
        }
        System.out.println("请求错误3："+e);
        return Result.error(500,"服务器错误");
    }
}
