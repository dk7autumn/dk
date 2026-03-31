package com.dk.salesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("permission")
public class Permission {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "name", exist = true)
    private String name;

    @TableField(value = "code", exist = true)
    private String code;

    @TableField(value = "type", exist = true)
    private Integer type = 1; // 1-菜单，2-按钮，3-接口

    @TableField(value = "parent_id", exist = true)
    private Long parentId = 0L;

    @TableField(value = "path", exist = true)
    private String path;

    @TableField(value = "icon", exist = true)
    private String icon;

    @TableField(value = "sort", exist = true)
    private Integer sort = 0;

    @TableField(value = "status", exist = true)
    private Integer status = 1;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
