package com.example.attendancesystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置类
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF（API 不需要）
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用默认登录页面
            .formLogin(AbstractHttpConfigurer::disable)
            // 禁用 HTTP Basic 认证
            .httpBasic(AbstractHttpConfigurer::disable)
            // 配置会话管理 - 允许使用 Session
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 允许匿名访问的接口
                .requestMatchers("/login", "/register", "/user/create", "/need-login").permitAll()
                // 允许访问静态资源和模板
                .requestMatchers("/", "/css/**", "/js/**", "/images/**", "/templates/images/**").permitAll()
                // 允许访问管理员 API（数据清除）- 放在前面
                .requestMatchers("/api/admin/**").permitAll()
                // 允许访问 student API（由拦截器验证 token）
                .requestMatchers("/student/api/**").permitAll()
                // 允许访问 teacher API
                .requestMatchers("/teacher/api/**").permitAll()
                // 允许访问 class API
                .requestMatchers("/api/class/**").permitAll()
                // 允许访问学生端页面
                .requestMatchers("/student/homepage", "/student/list-page", "/student/add", "/student/edit",
                    "/student/sign-in", "/student/ask-for-leave", "/student/course/select",
                    "/student/sign-records", "/student/list").permitAll()
                // 允许访问教师端页面
                .requestMatchers("/teacher/homepage", "/teacher/course/create", "/tch-sign-list",
                    "/teacher/class-list", "/tch-homepage", "/stu-homepage",
                    "/tch-course-create", "/stu-course-select", "/file-import", "/teacher/leave-manage").permitAll()
                // 其他所有请求需要认证
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
