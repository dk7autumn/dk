package com.dk.salesystem.service;

import com.dk.salesystem.entity.Fish;
import com.dk.salesystem.repository.FishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FishService {

    @Autowired
    private FishRepository fishRepository;

    public List<Fish> findAll() {
        return fishRepository.findAll();
    }

    public Optional<Fish> findById(Long id) {
        return fishRepository.findById(id);
    }

    public Fish save(Fish fish) {
        return fishRepository.save(fish);
    }

    public void deleteById(Long id) {
        fishRepository.deleteById(id);
    }

    public List<Fish> searchByName(String name) {
        return fishRepository.findByNameContaining(name);
    }
}
