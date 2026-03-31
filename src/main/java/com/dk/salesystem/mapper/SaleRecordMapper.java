package com.dk.salesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dk.salesystem.entity.SaleRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SaleRecordMapper extends BaseMapper<SaleRecord> {

    @Select("SELECT * FROM sale_record WHERE sale_datetime >= #{start} AND sale_datetime <= #{end}")
    List<SaleRecord> findBySaleDateTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT SUM(total_price) FROM sale_record WHERE sale_datetime >= #{start} AND sale_datetime <= #{end}")
    java.math.BigDecimal sumBySaleDateTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
