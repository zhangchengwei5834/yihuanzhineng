package com.yihuankeji.config;

import com.yihuankeji.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://127.0.0.1:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 排除不需要token验证的路径
                .excludePathPatterns(
                        "/user/register",    // 注册接口
                        "/user/login",       // 登录接口
                        "/error",            // 错误页面
                        "/actuator/**",      // Spring Boot Actuator端点
                        "/favicon.ico",      // 网站图标
                        "/static/**",        // 静态资源
                        "/css/**",           // CSS文件
                        "/js/**",            // JavaScript文件
                        "/images/**"         // 图片文件
                );
    }
}