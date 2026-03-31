package com.dk.salesystem.service;

import com.dk.salesystem.entity.Fish;
import com.dk.salesystem.mapper.FishMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FishService {

    @Autowired
    private FishMapper fishMapper;

    public List<Fish> findAll() {
        return fishMapper.selectList(null);
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
