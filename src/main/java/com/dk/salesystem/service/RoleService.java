package com.dk.salesystem.service;

import com.dk.salesystem.entity.Role;
import com.dk.salesystem.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    public List<Role> findAll() {
        return roleMapper.selectList(null);
    }

    public Role findById(Long id) {
        return roleMapper.selectById(id);
    }

    public Role findByName(String name) {
        return roleMapper.findByName(name);
    }

    @Transactional
    public Role save(Role role) {
        role.setUpdatedAt(LocalDateTime.now());
        if (role.getId() == null) {
            roleMapper.insert(role);
        } else {
            roleMapper.updateById(role);
        }
        return role;
    }

    @Transactional
    public void deleteById(Long id) {
        roleMapper.deleteById(id);
    }
}
