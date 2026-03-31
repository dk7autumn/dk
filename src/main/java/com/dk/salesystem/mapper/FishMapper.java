package com.dk.salesystem.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dk.salesystem.entity.Fish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FishMapper extends BaseMapper<Fish> {

    @Select("SELECT * FROM fish WHERE name LIKE CONCAT('%', #{name}, '%')")
    List<Fish> findByNameContaining(@Param("name") String name);
}
