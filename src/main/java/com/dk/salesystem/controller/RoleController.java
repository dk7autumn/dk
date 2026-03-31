package com.dk.salesystem.controller;

import com.dk.salesystem.dto.ApiResponse;
import com.dk.salesystem.entity.Role;
import com.dk.salesystem.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@PreAuthorize("isAuthenticated()")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:query')")
    public ApiResponse<List<Role>> list() {
        return ApiResponse.success(roleService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public ApiResponse<Role> getById(@PathVariable Long id) {
        Role role = roleService.findById(id);
        if (role == null) {
            return ApiResponse.error("角色不存在");
        }
        return ApiResponse.success(role);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public ApiResponse<Role> add(@RequestBody Role role) {
        return ApiResponse.success(roleService.save(role));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public ApiResponse<Role> update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return ApiResponse.success(roleService.save(role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.deleteById(id);
        return ApiResponse.success();
    }
}
