package com.yihuankeji.dto;

public class AuthResponse {
    private String message;
    private String token;

    public AuthResponse() {}

    // 仅消息的构造函数（用于错误响应）
    public AuthResponse(String message) {
        this.message = message;
    }

    // 成功响应的构造函数
    public AuthResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}