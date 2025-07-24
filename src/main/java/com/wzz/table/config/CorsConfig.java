package com.wzz.table.config;

import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private static final Logger log = LogManager.getLogger(CorsConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("*")
//                .allowedOriginPatterns("*")
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("*")
//                 .allowCredentials(false)
//                .maxAge(3600);
        registry.addMapping("/**")
                .allowedOrigins("http://192.168.1.6:5173") // 实际前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false) // 禁用凭据支持
                .maxAge(3600);
    }

    /**
     * CORS 跨域处理策略
     */
    @Bean
    public SaCorsHandleFunction corsHandle() {
        return (req, res, sto) -> {
            res.setHeader("Access-Control-Allow-Origin", "*")                               // 允许指定域访问跨域资源
                    .setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE")// 允许所有请求方式
                    .setHeader("Access-Control-Max-Age", "3600")                            // 有效时间
                    .setHeader("Access-Control-Allow-Headers", "*");                        // 允许的header参数
            // 如果是预检请求，则立即返回到前端
            SaRouter.match(SaHttpMethod.OPTIONS)
                    .free(r -> log.info("--------OPTIONS预检请求，不做处理"))
                    .back();
        };
    }
}
