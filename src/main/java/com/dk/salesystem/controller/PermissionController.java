package com.dk.salesystem.controller;

import com.dk.salesystem.dto.ApiResponse;
import com.dk.salesystem.entity.Permission;
import com.dk.salesystem.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permission")
@PreAuthorize("isAuthenticated()")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:permission:query')")
    public ApiResponse<List<Permission>> list() {
        return ApiResponse.success(permissionService.findAll());
    }

    @GetMapping("/menu")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<List<Permission>> getMenuTree() {
        return ApiResponse.success(permissionService.findMenuTree());
    }

    @GetMapping("/{id}")
    public ApiResponse<Permission> getById(@PathVariable Long id) {
        Permission permission = permissionService.findById(id);
        if (permission == null) {
            return ApiResponse.error("权限不存在");
        }
        return ApiResponse.success(permission);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:add')")
    public ApiResponse<Permission> add(@RequestBody Permission permission) {
        return ApiResponse.success(permissionService.save(permission));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:edit')")
    public ApiResponse<Permission> update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        return ApiResponse.success(permissionService.save(permission));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.deleteById(id);
        return ApiResponse.success();
    }
}
