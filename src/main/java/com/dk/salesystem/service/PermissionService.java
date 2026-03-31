package com.dk.salesystem.service;

import com.dk.salesystem.entity.Permission;
import com.dk.salesystem.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;

    public List<Permission> findAll() {
        return permissionMapper.selectList(null);
    }

    public List<Permission> findMenuTree() {
        return permissionMapper.findByStatus(1);
    }

    public Permission findById(Long id) {
        return permissionMapper.selectById(id);
    }

    public List<Permission> findByParentId(Long parentId) {
        return permissionMapper.findByParentId(parentId);
    }

    public Permission save(Permission permission) {
        if (permission.getId() == null) {
            permissionMapper.insert(permission);
        } else {
            permissionMapper.updateById(permission);
        }
        return permission;
    }

    public void deleteById(Long id) {
        permissionMapper.deleteById(id);
    }
}
