package com.yihuankeji.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yihuankeji.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理OPTIONS请求（CORS预检请求）
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        
        if (token == null || token.isEmpty()) {
            sendErrorResponse(response, 401, "缺少Authorization头");
            return false;
        }

        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // 验证token格式
            if (!jwtUtil.isValidTokenFormat(token)) {
                sendErrorResponse(response, 401, "Token格式无效");
                return false;
            }

            // 检查token是否过期
            if (jwtUtil.isTokenExpired(token)) {
                sendErrorResponse(response, 401, "Token已过期");
                return false;
            }

            // 从token中获取用户信息
            String username = jwtUtil.getUsernameFromToken(token);
            Integer userId = jwtUtil.getUserIdFromToken(token);

            // 验证token
            if (!jwtUtil.validateToken(token, username)) {
                sendErrorResponse(response, 401, "Token验证失败");
                return false;
            }

            // 将用户信息存储到request中，供后续使用
            request.setAttribute("userId", userId);
            request.setAttribute("username", username);

            return true;

        } catch (Exception e) {
            sendErrorResponse(response, 401, "Token验证异常: " + e.getMessage());
            return false;
        }
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", message);
        
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}