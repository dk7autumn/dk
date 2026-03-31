package com.dk.salesystem.controller;

import com.dk.salesystem.dto.ApiResponse;
import com.dk.salesystem.entity.User;
import com.dk.salesystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@PreAuthorize("isAuthenticated()")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:user:query')")
    public ApiResponse<List<User>> list() {
        return ApiResponse.success(userService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public ApiResponse<User> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("用户不存在"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public ApiResponse<User> add(@RequestBody User user) {
        return ApiResponse.success(userService.save(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public ApiResponse<User> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return ApiResponse.success(userService.save(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.deleteById(id);
        return ApiResponse.success();
    }
}
