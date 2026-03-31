package com.dk.salesystem.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dk.salesystem.entity.Fish;
import com.dk.salesystem.mapper.FishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class FishService {

    @Autowired
    private FishMapper fishMapper;

    public List<Fish> findAll() {
        return fishMapper.selectList(null);
    }

    public Page<Fish> findPage(int page, int size, String name) {
        Page<Fish> pageParam = new Page<>(page, size);
        if (StringUtils.hasText(name)) {
            // 使用 QueryWrapper 进行模糊查询
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Fish> wrapper =
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            wrapper.like("name", name);
            return fishMapper.selectPage(pageParam, wrapper);
        }
        return fishMapper.selectPage(pageParam, null);
    }

    public Fish findById(Long id) {
        return fishMapper.selectById(id);
    }

    public Fish save(Fish fish) {
        if (fish.getId() == null) {
            fishMapper.insert(fish);
        } else {
            fishMapper.updateById(fish);
        }
        return fish;
    }

    public void deleteById(Long id) {
        fishMapper.deleteById(id);
    }

    public List<Fish> searchByName(String name) {
        return fishMapper.findByNameContaining(name);
    }
}
