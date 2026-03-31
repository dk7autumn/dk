package com.dk.salesystem.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@TableName("role")
public class Role {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "name", exist = true)
    private String name;

    @TableField(value = "code", exist = true)
    private String code;

    @TableField(value = "description", exist = true)
    private String description;

    @TableField(value = "status", exist = true)
    private Integer status = 1;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private List<Permission> permissions = new ArrayList<>();
}
