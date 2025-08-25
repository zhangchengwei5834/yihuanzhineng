package com.yihuankeji.controller;

import com.yihuankeji.dto.AuthResponse;
import com.yihuankeji.dto.AuthRequest;
import com.yihuankeji.pojo.User;
import com.yihuankeji.service.AccountService;
import com.yihuankeji.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class AccountController {

    private final AccountService accountService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AccountController(AccountService accountService, JwtUtil jwtUtil) {
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest req) {
        try {
            // 使用username作为name
            String name = req.getUsername();
            User user = accountService.register(req.getUsername(), req.getPassword(), name);
            
            // 注册成功后自动生成JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            AuthResponse body = new AuthResponse("注册成功", token);
            return new ResponseEntity<>(body, HttpStatus.CREATED);
        } catch (AccountService.UsernameAlreadyExistsException e) {
            AuthResponse body = new AuthResponse(e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.CONFLICT);
        } catch (IllegalArgumentException e) {
            AuthResponse body = new AuthResponse(e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        try {
            User user = accountService.login(req.getUsername(), req.getPassword());
            // 生成JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            AuthResponse body = new AuthResponse("登录成功", token);
            return new ResponseEntity<>(body, HttpStatus.OK);
        } catch (AccountService.InvalidCredentialsException e) {
            AuthResponse body = new AuthResponse("用户名或密码错误");
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            AuthResponse body = new AuthResponse(e.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpServletRequest request) {
        // 从拦截器中获取用户信息
        String username = (String) request.getAttribute("username");
        
        // 通过username查询完整的用户信息
        User user = accountService.findByUsername(username);
        if (user == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "用户不存在");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        // 创建用户信息对象
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("name", user.getName());
        
        // 创建响应对象
        Map<String, Object> response = new HashMap<>();
        response.put("user", userInfo);
        response.put("message", "Token通过");
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
