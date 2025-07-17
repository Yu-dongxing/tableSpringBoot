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
        System.out.println(e.getMessage());
        return Result.error("不支持的请求方法");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e){
        e.printStackTrace();
        System.out.println(e.getMessage());
        return Result.error("操作失败");
    }
    @ExceptionHandler(SaTokenException.class)
    public Result handlerSaTokenException(SaTokenException e) {
        // 根据不同异常细分状态码返回不同的提示 11002-3 11011-11016
        if(e.getCode() == 11001) {
        //return SaResult.error("未能读取到有效Token");
            return Result.error(101,"未能读取到有效Token");
        }
        if(e.getCode() == 11002) {
        //return SaResult.error("登录时的账号id值为空");
            return Result.error(101,"登录时的账号id值为空");
        }
        if(e.getCode() == 11003) {
        //return SaResult.error("更改 Token 指向的 账号Id 时，账号Id值为空");
            return Result.error(101,"更改 Token 指向的 账号Id 时，账号Id值为空");
        }
        if(e.getCode() == 11011) {
        //return SaResult.error("未能读取到有效Token");
            return Result.error(101,"未能读取到有效Token");
        }
        if(e.getCode() == 11012) {
        //return SaResult.error("Token无效");
            return Result.error(101,"Token无效");
        }
        if(e.getCode() == 11013) {
        //return SaResult.error("Token已过期");
            return Result.error(101,"Token已过期");
        }
        if(e.getCode() == 11014) {
        // return SaResult.error("Token已被顶下线");
            return Result.error(101,"Token已被顶下线");
        }
        if(e.getCode() == 11015) {
        //return SaResult.error("Token已被踢下线");
            return Result.error(101,"Token已被踢下线");
        }
        if(e.getCode() == 11016) {
        //return SaResult.error("Token已被冻结");
            return Result.error(101,"Token已被冻结");
        }
        if(e.getCode() == 11041) {
            return Result.error(403, "无此角色权限");
        }
        if(e.getCode() == 11042) {
            return Result.error(403, "无此角色权限：" + e.getMessage());
        }

        // 更多 code 码判断 ...

        System.out.println(e);
        // 默认的提示
        return Result.error(500,"服务器错误");
    }
}
