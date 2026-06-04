package com.example.attendancesystem.config;

import com.example.attendancesystem.interceptor.LoginInterceptor;
import com.example.attendancesystem.interceptor.PageLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private PageLoginInterceptor pageLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // API 接口拦截 - 验证 Token
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/student/api/**", "/teacher/api/**")
                .excludePathPatterns("/student/api/login", "/teacher/api/login");

        // 页面访问拦截 - 验证登录状态
        registry.addInterceptor(pageLoginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/register", "/", "/css/**", "/js/**", "/images/**", "/templates/images/**", "/user/create");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射 templates/images 目录下的静态资源
        registry.addResourceHandler("/templates/images/**")
                .addResourceLocations("classpath:/templates/images/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 配置 CORS，允许跨域请求
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
