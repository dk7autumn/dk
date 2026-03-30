package com.dk.salesystem.repository;

import com.dk.salesystem.entity.Fish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FishRepository extends JpaRepository<Fish, Long> {
    List<Fish> findByNameContaining(String name);
}
