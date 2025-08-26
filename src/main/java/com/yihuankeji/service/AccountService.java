package com.yihuankeji.service;

import com.yihuankeji.mapper.AccountMapper;
import com.yihuankeji.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
public class AccountService {

    private final AccountMapper accountMapper;

    @Autowired
    public AccountService(AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Transactional
    public User register(String username, String password, String name) {
        // 基本校验
        if (isBlank(username) || isBlank(password) || isBlank(name)) {
            throw new IllegalArgumentException("用户名、密码、姓名均不能为空");
        }
        username = username.trim();
        name = name.trim();
        if (username.length() < 3 || username.length() > 50) {
            throw new IllegalArgumentException("用户名长度需在3-50之间");
        }
        if (password.length() < 6 || password.length() > 100) {
            throw new IllegalArgumentException("密码长度需在6-100之间");
        }
        // 查重
        Integer count = accountMapper.checkUsernameExist(username);
        if (count != null && count > 0) {
            throw new UsernameAlreadyExistsException("该账号已被注册");
        }
        // 构造并保存
        User user = new User();
        user.setUsername(username);
        user.setPassword(sha256Hex(password));
        user.setName(name);
        user.setRegisterTime(LocalDateTime.now());
        int result = accountMapper.register(user);
        if (result <= 0 || user.getId() == null) {
            throw new RuntimeException("注册失败");
        }
        // 不返回密码
        user.setPassword(null);
        return user;
    }

    public User login(String username, String password) {
        // 基本校验
        if (isBlank(username) || isBlank(password)) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }
        username = username.trim();
        
        // 查询用户
        User user = accountMapper.findByUsername(username);
        if (user == null) {
            throw new InvalidCredentialsException("用户名或密码错误");
        }
        
        // 验证密码
        String hashedPassword = sha256Hex(password);
        if (!hashedPassword.equals(user.getPassword())) {
            throw new InvalidCredentialsException("用户名或密码错误");
        }
        
        // 不返回密码
        user.setPassword(null);
        return user;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                String hex = Integer.toHexString((b & 0xff));
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 不可用", e);
        }
    }

    public User findByUsername(String username) {
        // 基本校验
        if (isBlank(username)) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        username = username.trim();
        
        // 查询用户
        User user = accountMapper.findByUsername(username);
        if (user != null) {
            // 不返回密码
            user.setPassword(null);
        }
        return user;
    }

    // 自定义业务异常
    public static class UsernameAlreadyExistsException extends RuntimeException {
        public UsernameAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class InvalidCredentialsException extends RuntimeException {
        public InvalidCredentialsException(String message) {
            super(message);
        }
    }
}
