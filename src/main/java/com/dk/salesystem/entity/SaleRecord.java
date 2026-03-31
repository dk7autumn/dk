package com.dk.salesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sale_record")
public class SaleRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "fish_id")
    private Long fishId;

    @TableField(value = "quantity")
    private BigDecimal quantity;

    @TableField(value = "unit_price")
    private BigDecimal unitPrice;

    @TableField(value = "total_price")
    private BigDecimal totalPrice;

    @TableField(value = "sale_datetime")
    private LocalDateTime saleDatetime;

    @TableField(value = "remark")
    private String remark;

    @TableField(exist = false)
    private Fish fish;
}
