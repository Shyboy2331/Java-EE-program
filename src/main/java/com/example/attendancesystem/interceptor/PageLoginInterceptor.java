package com.example.attendancesystem.interceptor;

import com.example.attendancesystem.data.User;
import com.example.attendancesystem.service.impl.StudentServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 页面访问登录验证拦截器
 * 用于拦截页面访问请求，验证用户是否已登录
 */
@Component
public class PageLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求 URI
        String uri = request.getRequestURI();

        // 放行登录页和注册页
        if (uri.equals("/login") || uri.equals("/register") || uri.equals("/")) {
            return true;
        }

        // 放行 API 请求（由 LoginInterceptor 处理）
        if (uri.startsWith("/student/api/") || uri.startsWith("/teacher/api/") || uri.startsWith("/api/")) {
            return true;
        }

        // 从 Session 中获取用户信息
        User user = (User) request.getSession().getAttribute("currentUser");
        
        if (user == null) {
            // 尝试从 Token 获取（兼容 API 请求）
            String token = request.getHeader("Authorization");
            if (token != null && !token.isEmpty()) {
                if (token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }
                user = StudentServiceImpl.validateToken(token);
                if (user != null) {
                    request.getSession().setAttribute("currentUser", user);
                }
            }
        }
        
        if (user == null) {
            // 未登录，重定向到提示页面
            response.sendRedirect("/need-login");
            return false;
        }
        
        return true;
    }
}
