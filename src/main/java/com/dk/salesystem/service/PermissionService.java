package com.dk.salesystem.service;

import com.dk.salesystem.entity.Permission;
import com.dk.salesystem.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    public List<Permission> findMenuTree() {
        // 返回菜单类型的权限，用于前端构建侧边栏
        return permissionRepository.findByStatus(1);
    }

    public Optional<Permission> findById(Long id) {
        return permissionRepository.findById(id);
    }

    public List<Permission> findByParentId(Long parentId) {
        return permissionRepository.findByParentId(parentId);
    }

    public Permission save(Permission permission) {
        return permissionRepository.save(permission);
    }

    public void deleteById(Long id) {
        permissionRepository.deleteById(id);
    }
}
