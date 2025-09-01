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
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin()
        registry.addInterceptor(new SaInterceptor(handler -> StpUtil.checkLogin()))
                .addPathPatterns("/**") // 拦截所有路径
                .excludePathPatterns(    // 排除以下路径
                        "/api/user/login",
                        "/api/root/**",      // 重点：在这里排除你的路径
                        "/api/user/logout",
                        "/api/user/sign",
                        "/api/user/get",
                        "/api/points/add"
                );
    }
}
