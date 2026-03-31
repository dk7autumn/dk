package com.dk.salesystem.repository;

import com.dk.salesystem.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    List<Permission> findByStatus(Integer status);

    List<Permission> findByParentId(Long parentId);

    boolean existsByCode(String code);
}
