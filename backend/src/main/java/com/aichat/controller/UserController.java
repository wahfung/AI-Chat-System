package com.aichat.controller;

import com.aichat.dto.ApiResponse;
import com.aichat.entity.User;
import com.aichat.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(
            @RequestParam String username,
            @RequestParam String password) {
        User user = userService.login(username, password);

        if (user == null) {
            return ResponseEntity.ok(ApiResponse.error("用户名或密码错误"));
        }

        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("username", user.getUsername());
        data.put("nickname", user.getNickname());
        data.put("avatar", user.getAvatar());

        return ResponseEntity.ok(ApiResponse.success(data, "登录成功"));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserInfo(@PathVariable Long userId) {
        // For demo, return a mock user if not found
        Map<String, Object> data = new HashMap<>();
        data.put("id", userId);
        data.put("username", "demo");
        data.put("nickname", "演示用户");
        data.put("avatar", "/default-avatar.png");

        return ResponseEntity.ok(ApiResponse.success(data));
    }
}
