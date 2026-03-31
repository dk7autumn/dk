package com.dk.salesystem.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("sale_record")
public class SaleRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "fish_id")
    private Long fishId;

    @TableField(value = "quantity")
    private Integer quantity;

    @TableField(value = "total_price")
    private BigDecimal totalPrice;

    @TableField(value = "sale_date")
    private LocalDate saleDate;

    @TableField(exist = false)
    private Fish fish;
}
