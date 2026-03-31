package com.dk.salesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dk.salesystem.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM user WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM user WHERE email = #{email}")
    boolean existsByEmail(@Param("email") String email);

    @Select("SELECT role_id FROM user_role WHERE user_id = #{userId}")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    @Select("<script>SELECT * FROM role WHERE id IN " +
            "<foreach item='id' collection='roleIds' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<com.dk.salesystem.entity.Role> findRolesByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Select("<script>SELECT permission_id FROM role_permission WHERE role_id IN " +
            "<foreach item='id' collection='roleIds' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Long> findPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Select("<script>SELECT * FROM permission WHERE id IN " +
            "<foreach item='id' collection='permissionIds' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<com.dk.salesystem.entity.Permission> findPermissionsByPermissionIds(@Param("permissionIds") List<Long> permissionIds);
}
