package com.dk.salesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dk.salesystem.entity.SaleRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface SaleRecordMapper extends BaseMapper<SaleRecord> {

    @Select("SELECT * FROM sale_record WHERE sale_date = #{date}")
    List<SaleRecord> findBySaleDate(@Param("date") LocalDate date);

    @Select("SELECT * FROM sale_record WHERE sale_date BETWEEN #{start} AND #{end}")
    List<SaleRecord> findBySaleDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Select("SELECT SUM(total_price) FROM sale_record WHERE sale_date = #{date}")
    java.math.BigDecimal sumBySaleDate(@Param("date") LocalDate date);

    @Select("SELECT SUM(total_price) FROM sale_record WHERE sale_date BETWEEN #{start} AND #{end}")
    java.math.BigDecimal sumBySaleDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
