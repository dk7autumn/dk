package com.dk.salesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dk.salesystem.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT * FROM role WHERE name = #{name}")
    Role findByName(@Param("name") String name);

    @Select("SELECT * FROM role WHERE code = #{code}")
    Role findByCode(@Param("code") String code);

    @Select("SELECT COUNT(*) FROM role WHERE name = #{name}")
    boolean existsByName(@Param("name") String name);

    @Select("SELECT COUNT(*) FROM role WHERE code = #{code}")
    boolean existsByCode(@Param("code") String code);

    @Select("SELECT * FROM role_permission WHERE role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    @Select("<script>SELECT * FROM permission WHERE id IN " +
            "<foreach item='id' collection='permissionIds' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<com.dk.salesystem.entity.Permission> findPermissionsByPermissionIds(@Param("permissionIds") List<Long> permissionIds);
}
