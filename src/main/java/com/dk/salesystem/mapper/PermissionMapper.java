package com.dk.salesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dk.salesystem.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    @Select("SELECT * FROM permission WHERE status = #{status}")
    List<Permission> findByStatus(@Param("status") Integer status);

    @Select("SELECT * FROM permission WHERE parent_id = #{parentId}")
    List<Permission> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT COUNT(*) FROM permission WHERE code = #{code}")
    boolean existsByCode(@Param("code") String code);
}
