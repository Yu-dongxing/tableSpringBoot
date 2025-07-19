package com.wzz.table.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    //jwt配置
    @Bean
    public StpLogic getStpLogicJwt(){
        return new StpLogicJwtForSimple();
    }
    // 注册登陆，角色拦截器
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new SaInterceptor(handler ->{
            SaRouter.match("/**")
                    .notMatch("/api/user/login")
                    .notMatch("/api/root/financialrecord/*")
                    .notMatch("/api/user/logout")
                    .notMatch("/api/user/sign")
                    .notMatch("/api/user/get")

                    .check(r->{
                        StpUtil.checkLogin();
                    });
        })).addPathPatterns("/**");
    }
}
