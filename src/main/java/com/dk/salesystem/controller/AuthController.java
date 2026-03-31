package com.dk.salesystem.controller;

import com.dk.salesystem.dto.ApiResponse;
import com.dk.salesystem.dto.LoginRequest;
import com.dk.salesystem.dto.LoginResponse;
import com.dk.salesystem.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<Void> logout() {
        SecurityContextHolder.clearContext();
        return ApiResponse.success();
    }

    @GetMapping("/info")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<LoginResponse.UserInfo> getUserInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof com.dk.salesystem.entity.User) {
            com.dk.salesystem.entity.User user = (com.dk.salesystem.entity.User) principal;
            java.util.List<String> roles = user.getRoles().stream()
                    .map(role -> role.getCode())
                    .toList();
            java.util.List<String> permissions = user.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .toList();
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    user.getId(),
                    user.getUsername(),
                    user.getNickname(),
                    user.getAvatar(),
                    user.getEmail(),
                    roles,
                    permissions
            );
            return ApiResponse.success(userInfo);
        }
        return ApiResponse.error("未登录");
    }
}
